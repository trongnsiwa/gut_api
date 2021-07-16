package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Set;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ColorSizeRepositoryTest {
  
  @Autowired
  private ColorSizeRepository colorSizeRepository;

  @Autowired
  private ProductRepository productRepository;

  @Test
  public void testFindColorsByProductIdSuccess() {
    Long productId = 164L;
    List<Long> colorIds = colorSizeRepository.findColorsByProductId(productId);
    assertEquals(2, colorIds.size());
    assertEquals(true, colorIds.contains(1L));
    assertEquals(true, colorIds.contains(2L));
  }

  @Test
  public void testFindColorSizesByProductIdSuccess() {
    Long productId = 164L;
    Product product = productRepository.findById(productId).get();
    Set<ColorSize> colorSizes = colorSizeRepository.findColorSizesByProductId(product);
    assertEquals(4, colorSizes.size());
  }

  @Test
  public void testExistsJoiningColorSuccess() {
    assertEquals(true, colorSizeRepository.existsJoiningColor(2L));
  }
  

}
