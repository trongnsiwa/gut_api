package com.ecommerce.gut.converters;

import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.entity.Color;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ColorConverter {

  @Autowired
  private ModelMapper modelMapper;
  
  public ColorDTO convertToDto(Color color) {
    return modelMapper.map(color, ColorDTO.class);
  }

  public Color convertToEntity(ColorDTO colorDTO) {
    return modelMapper.map(colorDTO, Color.class);
  }

}
