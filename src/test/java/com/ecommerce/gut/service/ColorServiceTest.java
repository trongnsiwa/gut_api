package com.ecommerce.gut.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ColorSizeRepository;
import com.ecommerce.gut.service.impl.ColorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ColorServiceTest {
  
  @Mock
  private ColorRepository colorRepository;

  @Mock
  private ColorSizeRepository colorSizeRepository;

  @InjectMocks
  private ColorServiceImpl colorService;

  @BeforeEach
  public void setUp() {
    Color color = new Color();
    color.setId(1L);
    color.setName("Color 1");
    color.setSource("Source 1");

    Mockito.when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
    Mockito.when(colorRepository.existsById(1L)).thenReturn(true);
    Mockito.when(colorRepository.existsByName("Color 1")).thenReturn(true);

    Color color2 = new Color(1L, "Color 2", "Source 2");

    Mockito.when(colorRepository.save(any(Color.class)))
        .thenReturn(color2);
    
    Mockito.when(colorSizeRepository.existsJoiningColor(1L)).thenReturn(true);

  }

  @Test
  public void testCreateColorDuplicateNameFail() {
    Color color = new Color(2L, "Color 1", "Source 2");
    assertThrows(DuplicateDataException.class, () -> colorService.createColor(color));
  }

  @Test
  public void testCreateColorIdAlreadyTakenFail() {
    Color color = new Color(1L, "Color 1", "Source 2");
    assertThrows(DuplicateDataException.class, () -> colorService.createColor(color));
  }

  @Test
  public void testCreateColorSuccess() throws Exception {
    Color color = new Color(2L, "Color 2", "Source 2");
    assertTrue(colorService.createColor(color));
  }

  @Test
  public void testUpdateColorSuccess() throws Exception {
    Color color = new Color(1L, "Color 2", "Source 2");
    assertEquals(color.getName(), colorService.updateColor(color, 1L).getName());
  }

  @Test
  public void testDeleteColorFail() throws Exception {
    assertThrows(RestrictDataException.class, () -> colorService.deleteColor(1L));
  }


}
