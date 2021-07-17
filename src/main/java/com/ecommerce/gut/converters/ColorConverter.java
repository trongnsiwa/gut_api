package com.ecommerce.gut.converters;

import com.ecommerce.gut.dto.ColorDTO;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.exception.ConvertEntityDTOException;
import com.ecommerce.gut.payload.response.ErrorCode;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ColorConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ColorConverter.class);

  @Autowired
  private ModelMapper modelMapper;
  
  public ColorDTO convertToDto(Color color) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(color, ColorDTO.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Color to ColorDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public Color convertToEntity(ColorDTO colorDTO) throws ConvertEntityDTOException {
    try {
      return modelMapper.map(colorDTO, Color.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert ColorDTO to Color");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

}
