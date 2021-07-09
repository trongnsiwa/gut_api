package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTest {
  
  @Mock
  private ProductRepository productRepository;

  @Mock
  private ColorRepository colorRepository;

  @Mock
  private ProductImageRepository imageRepository;

  @Mock
  private ProductColorSizeRepository productColorSizeRepository;

  @InjectMocks
  private ProductService productService;

  // @Test
  // public void getProductsByCategoryIdPerPage() {
  //   Product product = new Product(1, "A", 7, "product1", "This is product 1", "material1", "handling1", "createdDate", updatedDate, brandNew, sale, priceSale, saleFromDate, saleToDate, category, productImages, colorSizes, colors)
  // }

}
