package com.ecommerce.gut.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.CreateProductDTO;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.UpdateProductDTO;
import com.ecommerce.gut.entity.Brand;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ColorSizeRepository;
import com.ecommerce.gut.repository.ImageRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.repository.BrandRepository;
import com.ecommerce.gut.service.ProductService;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;


@Service
public class ProductServiceImpl implements ProductService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ColorRepository colorRepository;

  @Autowired
  PSizeRepository pSizeRepository;

  @Autowired
  ProductImageRepository productImageRepository;

  @Autowired
  ImageRepository imageRepository;

  @Autowired
  CategoryRepository categoryRepository;
  
  @Autowired
  BrandRepository brandRepository;

  @Autowired
  ColorSizeRepository colorSizeRepository;

  @Override
  public List<Product> getProductsByCategoryIdPerPage(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy) throws LoadDataFailException, DataNotFoundException {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);
      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Category category = existedCategory.get();

      PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, generateSortProduct(sortBy));

      if (category.getParent() == null) {
        return productRepository.getProductsByParent(category, pageRequest).getContent();
      }

      return productRepository.getProductsByCategory(existedCategory.get(), pageRequest)
          .getContent();
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    } catch (Exception ex) {
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }
  }

  private Sort generateSortProduct(String sortBy) {
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

      return sort;
  }

  @Override
  public Product getProductDetail(Long id) throws DataNotFoundException {
    return productRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("Product {} is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
        });
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public boolean addProductToCategory(CreateProductDTO productDTO, Long categoryId)
      throws CreateDataFailException {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);
      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Optional<Brand> existedBrand = brandRepository.findById(productDTO.getBrandId());
      if (!existedBrand.isPresent()) {
        LOGGER.info("Brand {} is not found", productDTO.getBrandId());
        throw new DataNotFoundException(ErrorCode.ERR_BRAND_NOT_FOUND);
      }

      Product product = new Product(productDTO.getName(), productDTO.getPrice(), productDTO.getShortDesc(), productDTO.getLongDesc(), productDTO.getMaterial(), productDTO.getHandling(), productDTO.isSale(), productDTO.getPriceSale(), productDTO.getSaleFromDate(), productDTO.getSaleToDate());
      product.setBrandNew(true);
      product.setCategory(existedCategory.get());
      product.setBrand(existedBrand.get());
      product.setDeleted(false);
      product.setInStock(false);

      productDTO.getColors().stream().forEach(colorSize -> {
        Color color = colorRepository.findById(colorSize.getColorId())
            .orElseThrow(
                () -> {
                  LOGGER.info("Color {} is not found", colorSize.getColorId());
                  return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
                });

        colorSize.getSizes().entrySet().stream()
            .forEach(entry -> {
              PSize size = pSizeRepository.findById(entry.getKey())
                  .orElseThrow(
                      () -> {
                        LOGGER.info("Size {} is not found", entry.getKey());
                        return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                      });

              product.addColorSize(color, size, entry.getValue());
              if (entry.getValue() > 0) {
                product.setInStock(true);
              }
            });
      });

      productRepository.save(product);
    } catch (DataNotFoundException e) {
      String message = e.getMessage();
      if (message.equals(ErrorCode.ERR_CATEGORY_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_COLOR_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_SIZE_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_BRAND_NOT_FOUND);
      }
    } catch (Exception e) {
      LOGGER.info("Fail to create product {}", productDTO.getName());
      throw new CreateDataFailException(ErrorCode.ERR_PRODUCT_CREATED_FAIL);
    }

    return true;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public Product updateProduct(UpdateProductDTO productDTO, Long id, Long categoryId)
      throws UpdateDataFailException, DataNotFoundException {
    try {
      Optional<Category> existedCategory =
          categoryRepository.findById(categoryId);
      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Optional<Brand> existedBrand = brandRepository.findById(productDTO.getBrandId());
      if (!existedBrand.isPresent()) {
        LOGGER.info("Brand {} is not found", productDTO.getBrandId());
        throw new DataNotFoundException(ErrorCode.ERR_BRAND_NOT_FOUND);
      }

      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Product product = existedProduct.get();
      transferDataToExistProduct(product, productDTO);
      product.setCategory(existedCategory.get());
      product.setBrand(existedBrand.get());
      product.setInStock(false);

      Set<ColorSize> colorSizes = colorSizeRepository.findColorSizesByProductId(id);

      setColorAndSizeToProduct(product, productDTO, colorSizes);

      return productRepository.save(product);

    } catch (DataNotFoundException e) {
      String message = e.getMessage();

      if (message.equals(ErrorCode.ERR_SIZE_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_COLOR_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_PRODUCT_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CATEGORY_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_BRAND_NOT_FOUND);
      }

    } catch (Exception e) {
      LOGGER.info("Fail to update product {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_UPDATED_FAIL);
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public boolean deleteProduct(Long id) throws DeleteDataFailException, DataNotFoundException {
    try {
      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Product product = existedProduct.get();
      product.setDeleted(true);

      productRepository.save(product);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to delete product {}", id);
      throw new DeleteDataFailException(ErrorCode.ERR_PRODUCT_DELETED_FAIL);
    }

    return true;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public Optional<Product> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id)
      throws UpdateDataFailException, DuplicateDataException, DataNotFoundException {
    try {
      Optional<Product> existedProduct = productRepository.findById(id);
      if (!existedProduct.isPresent()) {
        LOGGER.info("Product {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      }

      Product product = existedProduct.get();

      Set<ProductImage> productImages = productImageRepository.findImagesByProduct(product);

      Collection<ProductImageDTO> images = imageListRequest.getImages();

      if (images.isEmpty()) {
        product.getProductImages().clear();
        return checkIfImagesRequestIsEmpty(productImages, product);
      }

      checkIfExistedTwoSameColors(imageListRequest);

      deleteIfNotReplacedImage(productImages, imageListRequest, product);

      imageListRequest.getImages().stream().forEach(img -> {
        if (img.getId() != null) {
          Optional<Image> foundedImage = imageRepository.findById(img.getId());
          if (foundedImage.isPresent()) {
            Image image = foundedImage.get();
            image.setImageUrl(img.getImageUrl());
            image.setTitle(img.getTitle());

            imageRepository.save(image);

            product.getProductImages().stream()
                .forEach(productImg -> {
                  if (productImg.getImage().equals(image)) {
                    productImg.setColorCode(img.getColorCode());
                  }
                });

          } else {
            saveImageBeforeAddToProduct(img, product);
          }
        } else {
          saveImageBeforeAddToProduct(img, product);
        }
      });

      return Optional.of(productRepository.save(product));
    } catch (DuplicateDataException e) {
      throw new DuplicateDataException(ErrorCode.ERR_NOT_EXIST_TWO_SAME_COLORS);
    } catch (DataNotFoundException e) {
      String message = e.getMessage();
      if (message.equals(ErrorCode.ERR_PRODUCT_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_PRODUCT_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
      }
    } catch (Exception e) {
      LOGGER.info("Fail to replace images of product {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_PRODUCT_IMAGES_REPLACED_FAIL);
    }
  }

  private void saveImageBeforeAddToProduct(ProductImageDTO img, Product product) {
    Image image = new Image(img.getImageUrl(), img.getTitle());

    imageRepository.save(image);

    product.addImage(image, img.getColorCode());
  }

  private void transferDataToExistProduct(Product product, UpdateProductDTO productDTO) {
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
    product.setDeleted(productDTO.isDeleted());
  }

  private void setColorAndSizeToProduct(Product product, UpdateProductDTO productDTO,
      Set<ColorSize> colorSizes) {
    product.getColorSizes().clear();

    productDTO.getColors().stream().forEach(colorSize -> {
      Color color = colorRepository.findById(colorSize.getColorId())
          .orElseThrow(
              () -> {
                LOGGER.info("Color {} is not found", colorSize.getColorId());
                return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
              });

      colorSize.getSizes().entrySet().stream()
          .forEach(entry -> {
            PSize size = pSizeRepository.findById(entry.getKey())
                .orElseThrow(
                    () -> {
                      LOGGER.info("Size {} is not found", entry.getKey());
                      return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                    });

            if (colorSizes.stream()
                .noneMatch(cs -> cs.getColor().equals(color) && cs.getSize().equals(size))) {
              product.addColorSize(color, size, entry.getValue());
            } else {
              product.getColorSizes().add(
                  colorSizes.stream()
                      .filter(cs -> cs.getColor().equals(color) && cs.getSize().equals(size))
                      .collect(Collectors.toList()).get(0));
            }
            if (entry.getValue() > 0) {
              product.setInStock(true);
            }
          });
    });
  }

  private Optional<Product> checkIfImagesRequestIsEmpty(Set<ProductImage> productImages,
      Product product) {
    LOGGER.info("Delete all images of product {} successful", product.getId());

    Product savedProduct = productRepository.save(product);
    productImages.stream()
        .forEach(image -> imageRepository.deleteById(image.getImage().getId()));

    return Optional.of(savedProduct);
  }

  private void checkIfExistedTwoSameColors(ImageListDTO imageListRequest)
      throws DuplicateDataException {
    List<Long> colorCodes = new ArrayList<>();

    imageListRequest.getImages().stream().forEach(image -> {
      if (image.getColorCode() > -1) {
        Optional<Color> color = colorRepository.findById(image.getColorCode());

        colorCodes.add(image.getColorCode());

        if (!color.isPresent()) {
          LOGGER.info("Color {} is not found", image.getColorCode());
          throw new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
        }
      }
    });

    Set<Long> colorCodeSet = new HashSet<>(colorCodes);
    if (colorCodeSet.size() < colorCodes.size()) {
      LOGGER.info("Cannot exist two same colors in the list");
      throw new DuplicateDataException(ErrorCode.ERR_NOT_EXIST_TWO_SAME_COLORS);
    }
  }

  private void deleteIfNotReplacedImage(Set<ProductImage> productImages,
      ImageListDTO imageListRequest, Product product) {
    List<ProductImage> imagesToRemove = new ArrayList<>();

    productImages.stream().forEach(image -> {
      boolean notReplaced =
          imageListRequest.getImages().stream()
              .noneMatch(img -> img.getImageUrl().equals(image.getImage().getImageUrl()));

      if (notReplaced) {
        imagesToRemove.add(image);
      }
    });

    if (!imagesToRemove.isEmpty()) {
      imagesToRemove.stream().forEach(image -> {
        product.getProductImages().remove(image);
        imageRepository.deleteById(image.getImage().getId());
      });
    }
  }


}
