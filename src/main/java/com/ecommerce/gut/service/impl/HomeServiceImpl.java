package com.ecommerce.gut.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.SaleProductDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.HomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HomeServiceImpl.class);

  @Autowired
  private ProductRepository productRepository;

  @Override
  public List<ProductDTO> getNewProducts(Integer size) throws LoadDataFailException {
    try {
      List<Product> products = productRepository.getNewProducts(PageRequest.of(0, size));

      if (!products.isEmpty()) {
        return products.stream()
            .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(),
                product.getShortDesc(), this.getImageListFromProduct(product),
                this.getColorListFromProduct(product)))
            .collect(Collectors.toList());
      }
    } catch (Exception ex) {
      LOGGER.info("Fail to load new products");
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }

    return Collections.emptyList();
  }

  @Override
  public List<SaleProductDTO> getSaleProducts(Integer size) throws LoadDataFailException {
    try {
      List<Product> products = productRepository.getSaleProducts(PageRequest.of(0, size));

      if (!products.isEmpty()) {
        return products.stream()
            .map(product -> new SaleProductDTO(product.getId(), product.getName(),
                product.getPrice(),
                product.getShortDesc(), this.getImageListFromProduct(product),
                this.getColorListFromProduct(product), product.getPriceSale(),
                product.getSaleFromDate(), product.getSaleToDate()))
            .collect(Collectors.toList());
      }
    } catch (Exception ex) {
      LOGGER.info("Fail to load sale products");
      throw new LoadDataFailException(ErrorCode.ERR_PRODUCT_LOADED_FAIL);
    }

    return Collections.emptyList();
  }

  private List<ProductImageDTO> getImageListFromProduct(Product product) {
    return product.getProductImages().stream()
        .map(productImage -> {
          Image image = productImage.getImage();
          return new ProductImageDTO(image.getId(), image.getImageUrl(),
              image.getTitle(), productImage.getColorCode());
        })
        .collect(Collectors.toList());
  }

  private List<ColorDTO> getColorListFromProduct(Product product) {
    return product.getColorSizes().stream()
        .map(colorSize -> {
          Color color = colorSize.getColor();
          return new ColorDTO(color.getId(), color.getName(), color.getSource());
        })
        .collect(Collectors.toList());
  }

}
