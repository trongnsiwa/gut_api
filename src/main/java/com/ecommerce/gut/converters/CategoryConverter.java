package com.ecommerce.gut.converters;

import com.ecommerce.gut.dto.CategoryDTO;
import com.ecommerce.gut.dto.CategoryGroupDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {
  
  @Autowired
  private ModelMapper modelMapper;

  public Category convertCategoryToEntity(CategoryDTO categoryDTO) {
    return modelMapper.map(categoryDTO, Category.class);
  }

  public CategoryDTO convertCategoryToDto(Category category) {
    return modelMapper.map(category, CategoryDTO.class);
  }

  public CategoryGroupDTO convertCategoryGroupToDto(CategoryGroup categoryGroup) {
    return modelMapper.map(categoryGroup, CategoryGroupDTO.class);
  }

  public CategoryGroup convertCategoryGroupToEntity(CategoryGroupDTO categoryGroupDTO) {
    return modelMapper.map(categoryGroupDTO, CategoryGroup.class);
  }

}
