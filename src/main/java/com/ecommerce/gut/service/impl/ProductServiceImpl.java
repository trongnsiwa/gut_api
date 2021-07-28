package com.ecommerce.gut.service.impl;

import static com.ecommerce.gut.specification.ProductSpecification.categoryEquals;
import static com.ecommerce.gut.specification.ProductSpecification.nameContainsIgnoreCase;
import static com.ecommerce.gut.specification.ProductSpecification.parentEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
import com.ecommerce.gut.specification.ProductSpecification;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

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
  public List<Product> getProductsPerPage(Integer pageNumber, Integer pageSize, String sortBy) {
    PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, generateSortProduct(sortBy));

    return productRepository.findAll(pageRequest).getContent();
  }

  @Override
  public List<Product> searchProductsByName(Integer pageNumber, Integer pageSize, String sortBy,
      String name) {
    PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, generateSortProduct(sortBy));

    Specification<Product> searchSpec = nameContainsIgnoreCase(name);

    return productRepository.findAll(searchSpec, pageRequest).getContent();
  }

  @Override
  public Long countProducts() {
    return productRepository.count();
  }

  @Override
  public Long countProductsByName(String name) {
    Specification<Product> countSpec = ProductSpecification.nameContainsIgnoreCase(name);

    return productRepository.count(countSpec);
  }

  @Override
  public List<Product> searchProductsByCategoryAndName(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy, String name)
      throws LoadDataFailException, DataNotFoundException {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);

      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Category category = existedCategory.get();

      PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, generateSortProduct(sortBy));

      Specification<Product> parenSpec = parentEquals(category);
      Specification<Product> nameSpec = nameContainsIgnoreCase(name);

      if (Objects.isNull(category.getParent())) {
        return productRepository.findAll(Specification.where(parenSpec).and(nameSpec), pageRequest)
            .getContent();
      }

      Specification<Product> categorySpec = categoryEquals(category);

      return productRepository
          .findAll(Specification.where(categorySpec).and(nameSpec), pageRequest)
          .getContent();
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    } catch (Exception ex) {
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }
  }

  @Override
  public Long countProductsByCategory(Long categoryId) {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);

      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Category category = existedCategory.get();

      Specification<Product> countParentSpec = parentEquals(category);

      if (Objects.isNull(category.getParent())) {
        return productRepository.count(countParentSpec);
      }

      Specification<Product> countSpec = categoryEquals(category);

      return productRepository.count(countSpec);
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    }
  }

  @Override
  public Long countProductsByCategoryAndName(Long categoryId, String name) {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);

      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Category category = existedCategory.get();

      Specification<Product> parenSpec = parentEquals(category);
      Specification<Product> nameSpec = nameContainsIgnoreCase(name);

      if (Objects.isNull(category.getParent())) {
        return productRepository.count(Specification.where(parenSpec).and(nameSpec));
      }

      Specification<Product> categorySpec = categoryEquals(category);

      return productRepository.count(Specification.where(categorySpec).and(categorySpec));
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    }
  }

  @Override
  public List<Product> getProductsByCategoryPerPage(Long categoryId, Integer pageNumber,
      Integer pageSize, String sortBy) throws LoadDataFailException, DataNotFoundException {
    try {
      Optional<Category> existedCategory = categoryRepository.findById(categoryId);

      if (!existedCategory.isPresent()) {
        LOGGER.info("Category {} is not found", categoryId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Category category = existedCategory.get();

      PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, generateSortProduct(sortBy));

      Specification<Product> findParentSpec = parentEquals(category);

      if (Objects.isNull(category.getParent())) {
        return productRepository.findAll(findParentSpec, pageRequest).getContent();
      }

      Specification<Product> findSpec = categoryEquals(category);

      return productRepository.findAll(findSpec, pageRequest)
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
        List<Order> cheapOrders = new ArrayList<>();
        cheapOrders.add(new Order(Direction.ASC, "price"));
        cheapOrders.add(new Order(Direction.ASC, "priceSale", Sort.NullHandling.NULLS_LAST));
        cheapOrders.add(new Order(Direction.ASC, "saleFromDate", Sort.NullHandling.NULLS_LAST));
        cheapOrders.add(new Order(Direction.ASC, "saleToDate", Sort.NullHandling.NULLS_LAST));
        cheapOrders.add(new Order(Direction.DESC, "brandNew"));
        cheapOrders.add(new Order(Direction.DESC, "updatedDate"));

        sort = Sort.by(cheapOrders);
        break;
      case "HIGHEST":
        List<Order> highOrders = new ArrayList<>();
        highOrders.add(new Order(Direction.DESC, "price"));
        highOrders.add(new Order(Direction.DESC, "brandNew"));
        highOrders.add(new Order(Direction.DESC, "priceSale", Sort.NullHandling.NULLS_LAST));
        highOrders.add(new Order(Direction.ASC, "saleFromDate", Sort.NullHandling.NULLS_LAST));
        highOrders.add(new Order(Direction.ASC, "saleToDate", Sort.NullHandling.NULLS_LAST));
        highOrders.add(new Order(Direction.DESC, "updatedDate"));

        sort = Sort.by(highOrders);
        break;
      case "A-Z":
        sort = Sort.by("name").ascending();
        break;
      case "Z-A":
        sort = Sort.by("name").descending();
        break;
      default:
        List<Order> defaultOrders = new ArrayList<>();
        defaultOrders.add(new Order(Direction.ASC, "price"));
        defaultOrders.add(new Order(Direction.DESC, "brandNew"));
        defaultOrders.add(new Order(Direction.ASC, "priceSale", Sort.NullHandling.NULLS_LAST));
        defaultOrders.add(new Order(Direction.ASC, "saleFromDate", Sort.NullHandling.NULLS_LAST));
        defaultOrders.add(new Order(Direction.ASC, "saleToDate", Sort.NullHandling.NULLS_LAST));
        defaultOrders.add(new Order(Direction.DESC, "updatedDate"));
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

      Product product = Product.builder()
          .name(productDTO.getName())
          .price(productDTO.getPrice())
          .shortDesc(productDTO.getShortDesc())
          .longDesc(productDTO.getLongDesc())
          .material(productDTO.getMaterial())
          .handling(productDTO.getHandling())
          .sale(productDTO.isSale())
          .priceSale(productDTO.getPriceSale())
          .saleFromDate(productDTO.getSaleFromDate())
          .saleToDate(productDTO.getSaleToDate())
          .brandNew(true)
          .category(existedCategory.get())
          .brand(existedBrand.get())
          .deleted(false)
          .inStock(false)
          .build();

      productDTO.getColors().stream().forEach(colorSize -> {
        Color color = colorRepository.findById(colorSize.getColorId())
            .orElseThrow(
                () -> {
                  LOGGER.info("Color {} is not found", colorSize.getColorId());
                  return new DataNotFoundException(ErrorCode.ERR_COLOR_NOT_FOUND);
                });

        colorSize.getSizes().stream()
            .forEach(size -> {
              PSize psize = pSizeRepository.findById(size.getSizeId())
                  .orElseThrow(
                      () -> {
                        LOGGER.info("Size {} is not found", size.getSizeId());
                        return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                      });

              product.addColorSize(color, psize, size.getQuantity());
              if (size.getQuantity() > 0) {
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

  @Override
  public Product updateProduct(UpdateProductDTO productDTO, Long id, Long categoryId)
      throws UpdateDataFailException, DataNotFoundException {
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

      Set<ColorSize> colorSizes = null;

      if (!productDTO.getColors().isEmpty()) {
        colorSizes = colorSizeRepository.findColorSizesByProductId(id);
      }

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

      colorSize.getSizes().stream()
          .forEach(size -> {
            PSize psize = pSizeRepository.findById(size.getSizeId())
                .orElseThrow(
                    () -> {
                      LOGGER.info("Size {} is not found", size.getSizeId());
                      return new DataNotFoundException(ErrorCode.ERR_SIZE_NOT_FOUND);
                    });

            if (colorSizes.stream()
                .noneMatch(cs -> cs.getColor().equals(color) && cs.getSize().equals(psize))) {
              product.addColorSize(color, psize, size.getQuantity());
            } else {
              product.getColorSizes().add(
                  colorSizes
                      .stream()
                      .filter(cs -> cs.getColor().equals(color) && cs.getSize().equals(psize))
                      .collect(Collectors.toList()).get(0));
            }
            if (size.getQuantity() > 0) {
              product.setInStock(true);
            }
          });
    });
  }

  private Optional<Product> checkIfImagesRequestIsEmpty(Set<ProductImage> productImages,
      Product product) {
    LOGGER.info("Delete all images of product {} successful", product.getId());

    Product savedProduct = productRepository.save(product);

    productImages.stream().forEach(image -> imageRepository.deleteById(image.getImage().getId()));

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
