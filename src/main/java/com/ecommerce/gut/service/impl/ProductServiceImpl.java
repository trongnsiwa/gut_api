package com.ecommerce.gut.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.ecommerce.gut.dto.ProductImageDto;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductColor;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.payload.request.ImageListRequest;
import com.ecommerce.gut.payload.request.ProductRequest;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.ProductService;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ColorRepository colorRepository;

  @Autowired
  PSizeRepository pSizeRepository;

  @Autowired
  ProductImageRepository imageRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CustomResponseEntity customResponseEntity;

  @Override
  public ResponseEntity<?> getProductsByCategoryIdPerPage(Long categoryId, int pageNumber,
      int pageSize, String sortBy) {
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
                .by(new Sort.Order(Sort.Direction.ASC, "price_sale",
                    Sort.NullHandling.NULLS_LAST))
                .and(
                    Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_from_date",
                        Sort.NullHandling.NULLS_LAST))
                        .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_to_date",
                            Sort.NullHandling.NULLS_LAST)))))
            .and(Sort.by("brand_new").descending())
            .and(Sort.by("updated_date").descending());
        break;
      case "HIGHEST":
        sort = Sort.by("price").descending()
            .and(Sort.by("brand_new").descending())
            .and(Sort.by("sale").descending()
                .and(Sort
                    .by(new Sort.Order(Sort.Direction.DESC, "price_sale",
                        Sort.NullHandling.NULLS_LAST))
                    .and(
                        Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_from_date",
                            Sort.NullHandling.NULLS_LAST))
                            .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_to_date",
                                Sort.NullHandling.NULLS_LAST))))))
            .and(Sort.by("updated_date").descending());
        break;
      default:
        sort = Sort.by("brand_new").descending()
            .and(Sort.by("sale").descending())
            .and(
                Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_from_date",
                    Sort.NullHandling.NULLS_LAST))
                    .and(Sort.by(new Sort.Order(Sort.Direction.ASC, "sale_to_date",
                        Sort.NullHandling.NULLS_LAST))))
            .and(Sort.by("updated_date").descending());
        break;
    }

    PageRequest request = PageRequest.of(pageNumber - 1, pageSize, sort);
    return ResponseEntity
        .ok(productRepository.getProductsByCategoryId(categoryId, request).getContent());
  }

  @Override
  public Product getProductDetail(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format("Product %d", id)));
  }

  @Override
  public ResponseEntity<?> addProductToCategory(ProductRequest productRequest, Long categoryId) {
    Optional<Category> existedCategory =
        categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(
          String.format("Category %d", categoryId));
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

    productRequest.getColorSizes().stream().forEach(colorSize -> {
      Color color = colorRepository.findById(colorSize.getColorId())
          .orElseThrow(
              () -> new CustomNotFoundException(String.format("Color %d", colorSize.getColorId())));

      ProductColor productColor = new ProductColor(product, color);

      Map<Integer, Integer> sizes = colorSize.getSizes();

      sizes.entrySet().stream()
          .forEach(entry -> {
            Optional<PSize> pSize = pSizeRepository.findById(entry.getKey());
            if (!pSize.isPresent()) {
              throw new CustomNotFoundException(String.format("Size %d", entry.getKey()));
            }

            productColor.addColorSize(pSize.get(), entry.getValue());
          });
      
      color.getProducts().add(productColor);
      productColor.setColor(color);
      product.addColor(productColor);
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
    product.getColors().clear();

    productRequest.getColorSizes().stream().forEach(colorSize -> {
      Color color = colorRepository.findById(colorSize.getColorId())
          .orElseThrow(
              () -> new CustomNotFoundException(String.format("Color %d", colorSize.getColorId())));

      ProductColor productColor = new ProductColor(product, color);

      Map<Integer, Integer> sizes = colorSize.getSizes();

      sizes.entrySet().stream()
          .forEach(entry -> {
            Optional<PSize> pSize = pSizeRepository.findById(entry.getKey());
            if (!pSize.isPresent()) {
              throw new CustomNotFoundException(String.format("Size %d", entry.getKey()));
            }

            productColor.addColorSize(pSize.get(), entry.getValue());
          });
      
      color.getProducts().add(productColor);
      productColor.setColor(color);
      product.addColor(productColor);
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
    product.getColors().clear();

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
  public ResponseEntity<?> replaceImagesOfProduct(ImageListRequest imageListRequest, Long id) {
    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format("Product %d", id));
    }

    Collection<ProductImageDto> images = imageListRequest.getImages();

    if (images.isEmpty()) {
      imageRepository.deleteAllByProductId(id);

      return customResponseEntity.generateMessageResponseEntity(
          String.format("Delete all images of product %d successful.", id), HttpStatus.OK);
    }

    imageListRequest.getImages().stream().forEach(image -> {
      Optional<Color> color = colorRepository.findById(image.getColorCode());

      if (!color.isPresent()) {
        throw new CustomNotFoundException(String.format("Color %d", image.getColorCode()));
      }
    });

    List<ProductImage> existedImages = imageRepository.findAllImageIdsByProductId(id);

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
