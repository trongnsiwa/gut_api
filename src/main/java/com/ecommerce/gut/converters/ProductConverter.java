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
import com.ecommerce.gut.entity.ProductColorSize;
import com.ecommerce.gut.entity.ProductImage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

  @Autowired
  private ModelMapper modelMapper;
  
   public ProductDetailDTO convertProductDetailToDto(Product product) {
    ProductDetailDTO productDetailDTO = modelMapper.map(product, ProductDetailDTO.class);
    productDetailDTO.setProductImages(
        product.getProductImages().stream()
            .map(this::convertProductImageToDto)
            .collect(Collectors.toList())
    );
    productDetailDTO.setColorSizes(
        product.getColorSizes().stream()
            .map(this::convertProductColorSizeToDto)
            .collect(Collectors.toSet())    
    );

    return productDetailDTO;
  }

  public PagingProductDTO convertPagingProductToDto(Product product) {
    PagingProductDTO pagingProductDTO = modelMapper.map(product, PagingProductDTO.class);
    pagingProductDTO.setImages(
        product.getProductImages().stream()
            .map(this::convertProductImageToDto)
            .collect(Collectors.toList()));
    pagingProductDTO.setColors(
        product.getColors().stream()
            .map(this::convertColorToDto)
            .collect(Collectors.toSet()));
    
    return pagingProductDTO;
  }

  public ProductImageDTO convertProductImageToDto(ProductImage productImage) {
    return modelMapper.map(productImage, ProductImageDTO.class);
  }

  public ProductColorSizeDTO convertProductColorSizeToDto(ProductColorSize productColorSize) {
    ColorDTO colorDTO = this.convertColorToDto(productColorSize.getColor());
    SizeDTO sizeDTO = this.convertSizeToDto(productColorSize.getSize());
    return new ProductColorSizeDTO(colorDTO, sizeDTO);
  }

  public ColorDTO convertColorToDto(Color color) {
    return modelMapper.map(color, ColorDTO.class);
  }

  public SizeDTO convertSizeToDto(PSize pSize) {
    return modelMapper.map(pSize, SizeDTO.class);
  }

}
