package com.ecommerce.gut.converters;

import java.util.stream.Collectors;

import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.PagingProductDTO;
import com.ecommerce.gut.dto.ProductColorSizeDTO;
import com.ecommerce.gut.dto.ProductDetailDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.ReviewDTO;
import com.ecommerce.gut.dto.SizeDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.entity.Review;
import com.ecommerce.gut.exception.ConvertEntityDTOException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.entity.ColorSize;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductConverter.class);

  @Autowired
  private ModelMapper modelMapper;

  public ProductDetailDTO convertProductDetailToDto(Product product)
      throws ConvertEntityDTOException {
    try {

      ProductDetailDTO productDetailDTO = ProductDetailDTO.builder()
          .id(product.getId())
          .name(product.getName())
          .price(product.getPrice())
          .shortDesc(product.getShortDesc())
          .longDesc(product.getLongDesc())
          .material(product.getMaterial())
          .handling(product.getHandling())
          .brandNew(product.isBrandNew())
          .sale(product.isSale())
          .priceSale(product.getPriceSale())
          .saleFromDate(product.getSaleFromDate())
          .saleToDate(product.getSaleToDate())
          .deleted(product.isDeleted())
          .build();

      productDetailDTO.setProductImages(
          product.getProductImages()
              .stream()
              .map(this::convertProductImageToDto)
              .collect(Collectors.toList()));

      productDetailDTO.setColorSizes(
          product.getColorSizes()
              .stream()
              .map(this::convertProductColorSizeToDto)
              .collect(Collectors.toSet()));

      productDetailDTO.setReviews(
          product.getReviews()
              .stream()
              .map(review -> convertReviewToDto(review, product))
              .collect(Collectors.toList()));

      productDetailDTO.setCategoryId(product.getCategory().getId());
      productDetailDTO.setCategoryName(product.getCategory().getName());
      productDetailDTO.setBrandId(product.getBrand().getId());
      productDetailDTO.setBrandName(product.getBrand().getName());

      return productDetailDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Product to ProductDetailDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public PagingProductDTO convertPagingProductToDto(Product product)
      throws ConvertEntityDTOException {
    try {
      PagingProductDTO pagingProductDTO = PagingProductDTO.builder()
          .id(product.getId())
          .name(product.getName())
          .price(product.getPrice())
          .shortDesc(product.getShortDesc())
          .brandNew(product.isBrandNew())
          .sale(product.isSale())
          .salePrice(product.getPriceSale())
          .saleFromDate(product.getSaleFromDate())
          .saleToDate(product.getSaleToDate())
          .deleted(product.isDeleted())
          .build();

      pagingProductDTO.setImages(
          product.getProductImages()
              .stream()
              .map(this::convertProductImageToDto)
              .filter(img -> img.getColorCode() >= 0)
              .collect(Collectors.toList()));

      pagingProductDTO.setColors(
          product.getColorSizes()
              .stream()
              .map(colorSize -> convertColorToDto(colorSize.getColor()))
              .collect(Collectors.toSet()));

      pagingProductDTO.setCategoryId(product.getCategory().getId());
      pagingProductDTO.setCategoryName(product.getCategory().getName());
      pagingProductDTO.setBrandId(product.getBrand().getId());
      pagingProductDTO.setBrandName(product.getBrand().getName());

      if (!product.getReviews().isEmpty()) {
        int rate = product.getReviews().stream().mapToInt(Review::getRating).sum();
        pagingProductDTO.setRate((double) Math.round(rate / product.getReviews().size()));
      }

      return pagingProductDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Product to PagingProductDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public ReviewDTO convertReviewToDto(Review review, Product product)
      throws ConvertEntityDTOException {
    try {
      ReviewDTO dto = new ReviewDTO();

      dto.setId(review.getId());
      dto.setProductId(product.getId());
      dto.setUserId(review.getUser().getId());
      dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
      dto.setTitle(review.getTitle());
      dto.setComment(review.getComment());
      dto.setRating(review.getRating());
      dto.setDatetime(review.getDatetime());

      return dto;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Review to ReviewDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public ProductImageDTO convertProductImageToDto(ProductImage productImage)
      throws ConvertEntityDTOException {
    try {
      ProductImageDTO imageDTO = new ProductImageDTO();

      imageDTO.setId(productImage.getImage().getId());
      imageDTO.setImageUrl(productImage.getImage().getImageUrl());
      imageDTO.setTitle(productImage.getImage().getTitle());
      imageDTO.setColorCode(productImage.getColorCode());

      return imageDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert ProductImage to ProductImageDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public ProductColorSizeDTO convertProductColorSizeToDto(ColorSize productColorSize)
      throws ConvertEntityDTOException {
    try {
      ColorDTO colorDTO = this.convertColorToDto(productColorSize.getColor());

      SizeDTO sizeDTO = this.convertSizeToDto(productColorSize.getSize());

      return new ProductColorSizeDTO(colorDTO, sizeDTO, productColorSize.getQuantity());
    } catch (Exception ex) {
      LOGGER.info("Fail to convert ColorSize to ProductColorSizeDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public ColorDTO convertColorToDto(Color color) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(color, ColorDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Color to ColorDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public SizeDTO convertSizeToDto(PSize pSize) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(pSize, SizeDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert PSize to SizeDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

}
