package com.ecommerce.gut.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductColorSize;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.payload.request.ProductRequest;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.ProductService;

import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private PSizeRepository pSizeRepository;

  @Autowired
  private ProductImageRepository imageRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Autowired
  private ProductColorSizeRepository productColorSizeRepository;

  @Override
  public List<Product> getProductsByCategoryIdPerPage(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy) {
    Optional<Category> existedCategory = categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(String.format("Category %d", categoryId));
    }

    Sort sort = null;
    switch (sortBy) {
      case "CHEAPEST":
        sort = Sort.by("price").ascending()
            .and(Sort.by("sale").descending())
            .and(Sort
                .by(new Sort.Order(Sort.Direction.ASC, "priceSale",
                    Sort.NullHandling.NULLS_LAST))
                .and(
                    Sort.by(new Sort.Order(Sort.Direction.ASC, "saleFromDate",
                        Sort.NullHandling.NULLS_LAST))
                        .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "saleToDate",
                            Sort.NullHandling.NULLS_LAST)))))
            .and(Sort.by("brandNew").descending())
            .and(Sort.by("updatedDate").descending());
        break;
      case "HIGHEST":
        sort = Sort.by("price").descending()
            .and(Sort.by("brandNew").descending())
            .and(Sort.by("sale").descending()
                .and(Sort
                    .by(new Sort.Order(Sort.Direction.DESC, "priceSale",
                        Sort.NullHandling.NULLS_LAST))
                    .and(
                        Sort.by(new Sort.Order(Sort.Direction.ASC, "saleFromDate",
                            Sort.NullHandling.NULLS_LAST))
                            .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "saleToDate",
                                Sort.NullHandling.NULLS_LAST))))))
            .and(Sort.by("updatedDate").descending());
        break;
      default:
        sort = Sort.by("brandNew").descending()
            .and(Sort.by("sale").descending())
            .and(
                Sort.by(new Sort.Order(Sort.Direction.ASC, "saleFromDate",
                    Sort.NullHandling.NULLS_LAST))
                    .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "saleToDate",
                        Sort.NullHandling.NULLS_LAST))))
            .and(Sort.by("updatedDate").descending());
        break;
    }

    PageRequest request = PageRequest.of(pageNumber - 1, pageSize, sort);
    List<Product> products =
        productRepository.getProductsByCategoryId(existedCategory.get(), request).getContent();
    return products.stream().map(product -> {
      Set<ProductColorSize> colorSizes =
          productColorSizeRepository.findColorSizesByProductId(product);

      Set<Color> colors = colorSizes.stream()
          .map(ProductColorSize::getColor)
          .collect(Collectors.toSet());

      List<ProductImage> images = colors.stream()
          .map(color -> imageRepository
              .findImageByProductIdAndColorCode(product.getId(), color.getId()).orElse(null))
          .collect(Collectors.toList());

      product.setColors(colors);
      product.setProductImages(images);
      return product;
    }).collect(Collectors.toList());
  }

  @Override
  public Product getProductDetail(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format("Product %d", id)));
  }

  @Override
  public ResponseEntity<?> addProductToCategory(ProductRequest productRequest, Long categoryId) {
    Optional<Category> existedCategory = categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(String.format("Category %d", categoryId));
    }

    if (productRequest.getId() > 0) {
      boolean existedProduct = productRepository.existsById(productRequest.getId());
      if (existedProduct) {
        return customResponseEntity.generateMessageResponseEntity(
            String.format("Product %d is already taken.", productRequest.getId()),
            HttpStatus.CONFLICT);
      }
    }

    Product product = new Product.Builder(productRequest.getName())
        .withPrice(productRequest.getPrice())
        .withDescription(productRequest.getShortDesc(), productRequest.getLongDesc(),
            productRequest.getMaterial(), productRequest.getHandling())
        .withNew(true)
        .withSale(productRequest.isSale(), productRequest.getPriceSale(),
            productRequest.getSaleFromDate(), productRequest.getSaleToDate())
        .withCategory(existedCategory.get())
        .build();

    productRequest.getColors().stream().forEach(colorSize -> {
      Color color = colorRepository.findById(colorSize.getColorId())
          .orElseThrow(
              () -> new CustomNotFoundException(String.format("Color %d", colorSize.getColorId())));

      colorSize.getSizes().entrySet().stream()
          .forEach(entry -> {
            PSize size = pSizeRepository.findById(entry.getKey())
                .orElseThrow(
                    () -> new CustomNotFoundException(String.format("Size %s", entry.getKey())));

            product.addColorSize(color, size, entry.getValue());
          });
    });

    productRepository.save(product);

    return customResponseEntity
        .generateMessageResponseEntity(String.format("Add product %s to category %s successful!",
            product.getName(), existedCategory.get().getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateProduct(ProductRequest productRequest, Long id, Long categoryId) {
    Optional<Category> existedCategory =
        categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(
          String.format("Category %d", categoryId));
    }

    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format("Product %d", id));
    }

    Product product = existedProduct.get();
    product.setName(productRequest.getName());
    product.setPrice(productRequest.getPrice());
    product.setShortDesc(productRequest.getShortDesc());
    product.setLongDesc(productRequest.getLongDesc());
    product.setMaterial(productRequest.getMaterial());
    product.setHandling(productRequest.getHandling());
    product.setBrandNew(false);
    product.setSale(productRequest.isSale());
    product.setPriceSale(productRequest.getPriceSale());
    product.setSaleFromDate(productRequest.getSaleFromDate());
    product.setSaleToDate(productRequest.getSaleToDate());
    product.setCategory(existedCategory.get());
    product.getColorSizes().clear();

    productRequest.getColors().stream().forEach(colorSize -> {
      Color color = colorRepository.findById(colorSize.getColorId())
          .orElseThrow(
              () -> new CustomNotFoundException(String.format("Color %d", colorSize.getColorId())));

      colorSize.getSizes().entrySet().stream()
          .forEach(entry -> {
            PSize size = pSizeRepository.findById(entry.getKey())
                .orElseThrow(
                    () -> new CustomNotFoundException(String.format("Size %s", entry.getKey())));

            product.addColorSize(color, size, entry.getValue());
          });
    });

    productRepository.save(product);

    return customResponseEntity.generateMessageResponseEntity(
        String.format("Update product %d successful!", id), HttpStatus.OK);
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteProduct(Long id) {
    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format("Product %d", id));
    }

    Product product = existedProduct.get();
    product.getProductImages().clear();
    product.getColorSizes().clear();

    Optional<Category> existedCategory = categoryRepository.findById(product.getCategory().getId());
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(
          String.format("Category %d", product.getCategory().getId()));
    }

    Category category = existedCategory.get();
    category.getProducts().remove(product);
    categoryRepository.save(category);

    return customResponseEntity.generateMessageResponseEntity(
        String.format("Delete category %d successful.", id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id) {
    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format("Product %d", id));
    }

    Collection<ProductImageDTO> images = imageListRequest.getImages();

    if (images.isEmpty()) {
      imageRepository.deleteAllByProductId(id);

      return customResponseEntity.generateMessageResponseEntity(
          String.format("Delete all images of product %d successful.", id), HttpStatus.OK);
    }

    List<Long> colorCodes = new ArrayList<>();

    imageListRequest.getImages().stream().forEach(image -> {
      if (image.getColorCode() > 0) {
        Optional<Color> color = colorRepository.findById(image.getColorCode());

        colorCodes.add(image.getColorCode());

        if (!color.isPresent()) {
          throw new CustomNotFoundException(String.format("Color %d", image.getColorCode()));
        }
      }
    });

    Set<Long> colorCodeSet = new HashSet<>(colorCodes);
    if (colorCodeSet.size() < colorCodes.size()) {
      return customResponseEntity.generateMessageResponseEntity(
          "Cannot exist two same colors in the images list.", HttpStatus.CONFLICT);
    }

    List<ProductImage> existedImages = imageRepository.findImagesByProductId(id);

    existedImages.stream().forEach(image -> {
      boolean notReplaced =
          imageListRequest.getImages().stream().noneMatch(img -> img.getId().equals(image.getId()));

      if (notReplaced) {
        imageRepository.deleteByIdAndProductId(Long.valueOf(String.valueOf(image.getId())), id);
      }
    });

    imageListRequest.getImages().stream().forEach(image -> imageRepository.findById(image.getId())
        .map(existedImage -> {
          existedImage.setImageUrl(image.getImageUrl());
          existedImage.setProduct(existedProduct.get());
          existedImage.setColorCode(image.getColorCode());

          return imageRepository.save(existedImage);
        })
        .orElseGet(() -> {
          var productImage =
              new ProductImage(image.getId(), existedProduct.get(), image.getImageUrl(),
                  image.getTitle(), image.getColorCode());

          return imageRepository.save(productImage);
        }));

    return customResponseEntity.generateMessageResponseEntity(
        String.format("Update images of product %d successful.", id), HttpStatus.OK);
  }

}
