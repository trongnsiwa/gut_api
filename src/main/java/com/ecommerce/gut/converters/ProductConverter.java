package com.ecommerce.gut.converters;

import java.util.stream.Collectors;
import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.PagingProductDTO;
import com.ecommerce.gut.dto.ProductColorSizeDTO;
import com.ecommerce.gut.dto.ProductDetailDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.SizeDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;
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
      ProductDetailDTO productDetailDTO = modelMapper.map(product, ProductDetailDTO.class);
      productDetailDTO.setProductImages(
          product.getProductImages().stream()
              .map(this::convertProductImageToDto)
              .collect(Collectors.toList()));
      productDetailDTO.setColorSizes(
          product.getColorSizes().stream()
              .map(this::convertProductColorSizeToDto)
              .collect(Collectors.toSet()));

      return productDetailDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Product to ProductDetailDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public PagingProductDTO convertPagingProductToDto(Product product)
      throws ConvertEntityDTOException {
    try {
      PagingProductDTO pagingProductDTO = modelMapper.map(product, PagingProductDTO.class);
      pagingProductDTO.setImages(
          product.getProductImages().stream()
              .map(this::convertProductImageToDto)
              .collect(Collectors.toList()));
      pagingProductDTO.setColors(
          product.getColorSizes().stream()
              .map(colorSize -> convertColorToDto(colorSize.getColor()))
              .collect(Collectors.toSet()));

      return pagingProductDTO;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Product to PagingProductDTO");
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
