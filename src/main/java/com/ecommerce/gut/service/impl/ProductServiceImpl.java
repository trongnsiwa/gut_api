package com.ecommerce.gut.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.context.MessageSource;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class ProductServiceImpl implements ProductService {

  private ProductRepository productRepository;

  private ColorRepository colorRepository;

  private PSizeRepository pSizeRepository;

  private ProductImageRepository imageRepository;

  private CategoryRepository categoryRepository;

  private CustomResponseEntity customResponseEntity;

  private ProductColorSizeRepository productColorSizeRepository;

  private MessageSource messages;

  private HttpServletRequest request;

  public ProductServiceImpl(ProductRepository productRepository, ColorRepository colorRepository,
      PSizeRepository pSizeRepository, ProductImageRepository imageRepository,
      CategoryRepository categoryRepository, CustomResponseEntity customResponseEntity,
      ProductColorSizeRepository productColorSizeRepository,
      MessageSource messages, HttpServletRequest request) {
    this.productRepository = productRepository;
    this.colorRepository = colorRepository;
    this.pSizeRepository = pSizeRepository;
    this.imageRepository = imageRepository;
    this.categoryRepository = categoryRepository;
    this.customResponseEntity = customResponseEntity;
    this.productColorSizeRepository = productColorSizeRepository;
    this.messages = messages;
    this.request = request;
  }

  @Override
  public List<Product> getProductsByCategoryIdPerPage(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy) {
    Optional<Category> existedCategory = categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, request.getLocale()), categoryId));
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

    PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sort);
    List<Product> products =
        productRepository.getProductsByCategoryId(existedCategory.get(), pageRequest).getContent();
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
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("product.message.notFound", null, request.getLocale()), id)));
  }

  @Override
  public ResponseEntity<?> addProductToCategory(ProductRequest productRequest, Long categoryId) {

    Locale locale = request.getLocale();

    Optional<Category> existedCategory = categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, locale), categoryId));
    }

    if (productRequest.getId() > 0) {
      boolean existedProduct = productRepository.existsById(productRequest.getId());
      if (existedProduct) {
        return customResponseEntity.generateMessageResponseEntity(
            String.format(messages.getMessage("product.message.alreadyTaken", null, locale), productRequest.getId()),
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
              () -> new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, locale), colorSize.getColorId())));

      colorSize.getSizes().entrySet().stream()
          .forEach(entry -> {
            PSize size = pSizeRepository.findById(entry.getKey())
                .orElseThrow(
                    () -> new CustomNotFoundException(String.format(messages.getMessage("size.message.notFound", null, locale), entry.getKey())));

            product.addColorSize(color, size, entry.getValue());
          });
    });

    productRepository.save(product);

    return customResponseEntity
        .generateMessageResponseEntity(String.format(messages.getMessage("product.message.addSucc", null, locale),
            product.getName(), existedCategory.get().getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateProduct(ProductRequest productRequest, Long id, Long categoryId) {

    Locale locale = request.getLocale();

    Optional<Category> existedCategory =
        categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(
          String.format(messages.getMessage("category.message.cateNotFound", null, locale), categoryId));
    }

    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("product.message.notFound", null, locale), id));
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
              () -> new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, locale), colorSize.getColorId())));

      colorSize.getSizes().entrySet().stream()
          .forEach(entry -> {
            PSize size = pSizeRepository.findById(entry.getKey())
                .orElseThrow(
                    () -> new CustomNotFoundException(String.format(messages.getMessage("size.message.notFound", null, locale), entry.getKey())));

            product.addColorSize(color, size, entry.getValue());
          });
    });

    productRepository.save(product);

    return customResponseEntity.generateMessageResponseEntity(
        String.format(messages.getMessage("product.message.updateSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  @Transactional
  public ResponseEntity<?> deleteProduct(Long id) {

    Locale locale = request.getLocale();

    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("product.message.notFound", null, locale), id));
    }

    Product product = existedProduct.get();
    product.getProductImages().clear();
    product.getColorSizes().clear();

    Optional<Category> existedCategory = categoryRepository.findById(product.getCategory().getId());
    if (!existedCategory.isPresent()) {
      throw new CustomNotFoundException(
          String.format(messages.getMessage("category.message.cateNotFound", null, locale), product.getCategory().getId()));
    }

    Category category = existedCategory.get();
    category.getProducts().remove(product);
    categoryRepository.save(category);

    return customResponseEntity.generateMessageResponseEntity(
        String.format(messages.getMessage("product.message.delSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id) {

    Locale locale = request.getLocale();

    Optional<Product> existedProduct = productRepository.findById(id);
    if (!existedProduct.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("product.message.notFound", null, locale), id));
    }

    Collection<ProductImageDTO> images = imageListRequest.getImages();

    if (images.isEmpty()) {
      imageRepository.deleteAllByProductId(id);

      return customResponseEntity.generateMessageResponseEntity(
          String.format(messages.getMessage("product.message.delAllImagesSucc", null, locale), id), HttpStatus.OK);
    }

    List<Long> colorCodes = new ArrayList<>();

    imageListRequest.getImages().stream().forEach(image -> {
      if (image.getColorCode() > 0) {
        Optional<Color> color = colorRepository.findById(image.getColorCode());

        colorCodes.add(image.getColorCode());

        if (!color.isPresent()) {
          throw new CustomNotFoundException(String.format(messages.getMessage("color.message.notFound", null, locale), image.getColorCode()));
        }
      }
    });

    Set<Long> colorCodeSet = new HashSet<>(colorCodes);
    if (colorCodeSet.size() < colorCodes.size()) {
      return customResponseEntity.generateMessageResponseEntity(
        messages.getMessage("product.message.notExistTwoSameColors", null, locale), HttpStatus.CONFLICT);
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
        String.format(messages.getMessage("product.message.updateImagesSucc", null, locale), id), HttpStatus.OK);
  }

}
