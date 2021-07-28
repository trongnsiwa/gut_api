package com.ecommerce.gut.converters;

import java.util.Set;
import java.util.stream.Collectors;
import com.ecommerce.gut.dto.CategoryDTO;
import com.ecommerce.gut.dto.CategoryParentDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.exception.ConvertEntityDTOException;
import com.ecommerce.gut.payload.response.ErrorCode;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryConverter.class);

  @Autowired
  private ModelMapper modelMapper;

  public Category convertCategoryToEntity(CategoryDTO categoryDTO)
      throws ConvertEntityDTOException {
    try {
      return modelMapper.map(categoryDTO, Category.class);
    } catch (Exception ex) {
      LOGGER.info("Fail to convert CategoryDTO to Category");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public CategoryDTO convertCategoryToDto(Category category) throws ConvertEntityDTOException {
    try {
      CategoryDTO dto = modelMapper.map(category, CategoryDTO.class);

      if (category.getParent() != null) {
        dto.setParentId(category.getParent().getId());
      }

      return dto;
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Category to CategoryDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

  public CategoryParentDTO convertCategoryParentToDto(Category category) {
    try {
      CategoryParentDTO categoryParentDTO = modelMapper.map(category, CategoryParentDTO.class);
      
      Set<CategoryDTO> subCategories = category.getSubCategories()
          .stream()
          .map(this::convertCategoryToDto)
          .collect(Collectors.toSet());

      categoryParentDTO.setSubCategories(subCategories);

      return categoryParentDTO;
      
    } catch (Exception ex) {
      LOGGER.info("Fail to convert Category to CategoryParentDTO");
      throw new ConvertEntityDTOException(ErrorCode.ERR_DATA_CONVERT_FAIL);
    }
  }

}
