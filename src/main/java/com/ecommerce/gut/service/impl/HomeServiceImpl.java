package com.ecommerce.gut.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.SaleProductDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ProductImage;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.HomeService;
import com.ecommerce.gut.temp.ProductTemp;
import com.ecommerce.gut.temp.SaleProductTemp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductImageRepository productImageRepository;

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private ProductColorSizeRepository productColorSizeRepository;

  @Override
  public List<ProductDTO> getNewProducts(Integer size) {
    List<ProductTemp> products = productRepository.getNewProducts(PageRequest.of(0, size));

    if (!products.isEmpty()) {
      return products.stream()
          .map(product -> {
            List<ProductImageDTO> images = productImageRepository.findImagesByProductId(product.getProductId()).stream()
                .map(image -> new ProductImageDTO(image.getId(), image.getImageUrl(),
                    image.getTitle(), image.getColorCode()))
                .collect(Collectors.toList());

            List<Color> colors = productColorSizeRepository.findColorsByProductId(product.getProductId()).stream()
                .map(colorId -> colorRepository.findById(colorId).orElseThrow(() -> new CustomNotFoundException(String.format("Color %d", colorId))))
                .collect(Collectors.toList());
            
            List<ColorDTO> colorDTOs = colors.stream().map(color -> new ColorDTO(color.getId(), color.getName(), color.getSource())).collect(Collectors.toList());

            return new ProductDTO(product.getProductId(), product.getProductName(), product.getPrice(),
                product.getShortDesc(), images, colorDTOs);
          })
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  @Override
  public List<SaleProductDTO> getSaleProducts(Integer size) {
    List<SaleProductTemp> products = productRepository.getSaleProducts(PageRequest.of(0, size));

    if (!products.isEmpty()) {
      return products.stream()
          .map(product -> {
            List<Color> colors = productColorSizeRepository.findColorsByProductId(product.getProductId()).stream()
                .map(colorId -> colorRepository.findById(colorId).orElseThrow(() -> new CustomNotFoundException(String.format("Color %d", colorId))))
                .collect(Collectors.toList());
            
            List<ColorDTO> colorDTOs = colors.stream().map(color -> new ColorDTO(color.getId(), color.getName(), color.getSource())).collect(Collectors.toList());

            List<ProductImageDTO> images = colors.stream()
                .map(color -> {

                  ProductImage image = productImageRepository
                      .findImageByProductIdAndColorCode(product.getProductId(), color.getId()).orElse(null);

                  return new ProductImageDTO(image.getId(), image.getImageUrl(),
                      image.getTitle(), image.getColorCode());

                })
                .collect(Collectors.toList());

            return new SaleProductDTO(product.getProductId(), product.getProductName(), product.getPrice(),
                product.getShortDesc(), images, colorDTOs, product.getPriceSale(), product.getSaleFromDate(), product.getSaleToDate());
          })
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

}
