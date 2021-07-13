package com.ecommerce.gut.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.CreateProductDTO;
import com.ecommerce.gut.dto.ErrorCode;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.UpdateProductDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductColorSize;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.ProductService;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ProductServiceImpl implements ProductService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

  private ProductRepository productRepository;

  private ColorRepository colorRepository;

  private PSizeRepository pSizeRepository;

  private ProductImageRepository imageRepository;

  private CategoryRepository categoryRepository;

  private ProductColorSizeRepository productColorSizeRepository;

  public ProductServiceImpl(ProductRepository productRepository, ColorRepository colorRepository,
      PSizeRepository pSizeRepository, ProductImageRepository imageRepository,
      CategoryRepository categoryRepository,
      ProductColorSizeRepository productColorSizeRepository) {
    this.productRepository = productRepository;
    this.colorRepository = colorRepository;
    this.pSizeRepository = pSizeRepository;
    this.imageRepository = imageRepository;
    this.categoryRepository = categoryRepository;
    this.productColorSizeRepository = productColorSizeRepository;
  }

  @Override
  public List<Product> getProductsByCategoryIdPerPage(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy) {
    Optional<Category> existedCategory = categoryRepository.findById(categoryId);
    if (!existedCategory.isPresent()) {
      LOGGER.info("Category %d is not found", categoryId);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
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
        .orElseThrow(() -> {
          LOGGER.info("Product %d is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        });
  }

  @Override
  public boolean addProductToCategory(CreateProductDTO productDTO, Long categoryId)
      throws CreateDataFailException {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);
      if (!existedCategory.isPresent()) {
        LOGGER.info("Category %d is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Product product = new Product(productDTO.getName(),
          productDTO.getPrice(),
          productDTO.getShortDesc(),
          productDTO.getLongDesc(),
          productDTO.getMaterial(),
          productDTO.getHandling(),
          true,
          productDTO.isSale(),
          productDTO.getPriceSale(),
          productDTO.getSaleFromDate(),
          productDTO.getSaleToDate(),
          existedCategory.get());

      product.setInStock(false);

      productDTO.getColors().stream().forEach(colorSize -> {
        Color color = colorRepository.findById(colorSize.getColorId())
            .orElseThrow(
                () -> {
                  LOGGER.info("Color %d is not found", colorSize.getColorId());
                  return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
                });

        colorSize.getSizes().entrySet().stream()
            .forEach(entry -> {
              PSize size = pSizeRepository.findById(entry.getKey())
                  .orElseThrow(
                      () -> {
                        LOGGER.info("Size %d is not found", entry.getKey());
                        return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                      });

              product.addColorSize(color, size, entry.getValue());
              if (entry.getValue() > 0) {
                product.setInStock(true);
              }
            });
      });

      productRepository.save(product);
    } catch (Exception e) {
      LOGGER.info("Fail to create product %s", productDTO.getName());
      throw new CreateDataFailException(ErrorCode.ERR_PRODUCT_CREATED_FAIL);
    }

    return true;
  }

  @Override
  public Product updateProduct(UpdateProductDTO productDTO, Long id, Long categoryId)
      throws UpdateDataFailException {
    try {
      Optional<Category> existedCategory =
          categoryRepository.findById(categoryId);
      if (!existedCategory.isPresent()) {
        LOGGER.info("Category %d is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Product product = existedProduct.get();
      product.setName(productDTO.getName());
      product.setPrice(productDTO.getPrice());
      product.setShortDesc(productDTO.getShortDesc());
      product.setLongDesc(productDTO.getLongDesc());
      product.setMaterial(productDTO.getMaterial());
      product.setHandling(productDTO.getHandling());
      product.setBrandNew(false);
      product.setSale(productDTO.isSale());
      product.setPriceSale(productDTO.getPriceSale());
      product.setSaleFromDate(productDTO.getSaleFromDate());
      product.setSaleToDate(productDTO.getSaleToDate());
      product.setCategory(existedCategory.get());
      product.getColorSizes().clear();

      product.setInStock(false);

      productDTO.getColors().stream().forEach(colorSize -> {
        Color color = colorRepository.findById(colorSize.getColorId())
            .orElseThrow(
                () -> {
                  LOGGER.info("Color %d is not found", colorSize.getColorId());
                  return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
                });

        colorSize.getSizes().entrySet().stream()
            .forEach(entry -> {
              PSize size = pSizeRepository.findById(entry.getKey())
                  .orElseThrow(
                      () -> {
                        LOGGER.info("Size %d is not found", entry.getKey());
                        return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                      });

              product.addColorSize(color, size, entry.getValue());
              if (entry.getValue() > 0) {
                product.setInStock(true);
              }
            });
      });

      return productRepository.save(product);
    } catch (Exception e) {
      LOGGER.info("Fail to update product %d", id);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_UPDATED_FAIL);
    }
  }

  @Override
  public boolean deleteProduct(Long id) throws DeleteDataFailException {
    try {
      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Product product = existedProduct.get();
      product.setDeleted(true);

      productRepository.save(product);
    } catch (Exception e) {
      LOGGER.info("Fail to delete product %d", id);
      throw new DeleteDataFailException(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
    }

    return true;
  }

  @Override
  public Optional<Product> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id) throws UpdateDataFailException {
    try {
      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Collection<ProductImageDTO> images = imageListRequest.getImages();

      if (images.isEmpty()) {
        LOGGER.info("Delete all images of product %d successful", id);
        imageRepository.deleteAllByProductId(id);

        Product product = existedProduct.get();
        product.setProductImages(Collections.emptyList());

        return Optional.of(product);
      }

      List<Long> colorCodes = new ArrayList<>();

      imageListRequest.getImages().stream().forEach(image -> {
        if (image.getColorCode() > 0) {
          Optional<Color> color = colorRepository.findById(image.getColorCode());

          colorCodes.add(image.getColorCode());

          if (!color.isPresent()) {
            LOGGER.info("Color %d is not found", image.getColorCode());
            throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
          }
        }
      });

      Set<Long> colorCodeSet = new HashSet<>(colorCodes);
      if (colorCodeSet.size() < colorCodes.size()) {
        LOGGER.info("Cannot exist two same colors in the list");
        throw new DuplicateDataException(ErrorCode.ERR_NOT_EXIST_TWO_SAME_COLORS);
      }

      List<ProductImage> existedImages = imageRepository.findImagesByProductId(id);

      existedImages.stream().forEach(image -> {
        boolean notReplaced =
            imageListRequest.getImages().stream()
                .noneMatch(img -> img.getId().equals(image.getId()));

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

      Optional<Product> updatedProduct = productRepository.findById(id);
      if (!updatedProduct.isPresent()) {
        LOGGER.info("Product %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      return updatedProduct;
    } catch (Exception e) {
      LOGGER.info("Fail to replace images of product %d", id);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_IMAGES_REPLACED_FAIL);
    }
  }

}
