package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ecommerce.gut.entity.Color;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ColorRepositoryTest {
  
  @Autowired
  private ColorRepository colorRepository;

  @Test
  public void testFindByIdSuccess() throws Exception {
    assertEquals(true, colorRepository.findById(163L).isPresent());
  }

  @Test
  public void testExistsByNameSuccess() throws Exception {
    assertEquals(true, colorRepository.existsByName("Yellow"));
  }

  @Test
  public void testSaveColorSuccess() throws Exception {
    Color color = new Color();
    color.setName("Yellow");
    color.setSource("source 1");

    assertNotNull(colorRepository.save(color)); 
  }

  @Test
  public void testUpdateColorSuccess() throws Exception {
    Color color = colorRepository.findById(163L).get();
    color.setSource("source 2");

    assertEquals("source 2", colorRepository.save(color).getSource());
  }

  @Test
  public void testDeleteColor() throws Exception {
    colorRepository.deleteById(163L);
    
    assertEquals(true, !colorRepository.findById(163L).isPresent());
  }

}
