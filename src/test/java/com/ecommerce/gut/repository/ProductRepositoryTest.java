package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductRepositoryTest {
  
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private PSizeRepository sizeRepository;

  @Autowired
  private ProductImageRepository imageRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ColorSizeRepository colorSizeRepository;

  @Test
  public void testSaveProductSuccess() throws Exception {
    Product product = new Product();
    product.setName("Product 1");
    product.setPrice(1000D);

    Category category = categoryRepository.findById(161L).get();
    product.setCategory(category);

    addColorSizeToProduct(1L, 1L, 1, product);
    addColorSizeToProduct(1L, 2L, 0, product);
    addColorSizeToProduct(1L, 3L, 2, product);
    addColorSizeToProduct(2L, 1L, 1, product);
    addColorSizeToProduct(2L, 2L, 0, product);
    addColorSizeToProduct(2L, 3L, 1, product);
    
    Product saveProduct = productRepository.save(product);

    assertNotNull(saveProduct);
    assertEquals(161L, saveProduct.getCategory().getId());
    assertEquals(6, saveProduct.getColorSizes().size());
  }

  private void addColorSizeToProduct(Long colorId, Long sizeId, int quantity, Product product) {
    Color color = colorRepository.findById(colorId).get();
    PSize size = sizeRepository.findById(sizeId).get();
    product.addColorSize(color, size, quantity);
  }


}
