package com.ecommerce.gut.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.repository.CategoryGroupRepository;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.service.CategoryService;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  CategoryGroupRepository categoryGroupRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CustomResponseEntity customResponseEntity;

  @Override
  public List<CategoryGroup> getAllCategoryGroups() {
    return categoryGroupRepository.findAll();
  }

  @Override
  public CategoryGroup getCategoryGroupById(Long groupId) {

    return categoryGroupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Category group is not found."));

  }

  @Override
  public ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup) {

    Map<String, String> messages = new HashMap<>();

    boolean existed = categoryGroupRepository.existsById(categoryGroup.getId());
    if (existed) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, false, "error", "Group ID is already taken.");
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, true, "error", String.format("Group name %s is already existed.", categoryGroup.getName()));
    }

    categoryGroupRepository.save(categoryGroup);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.CREATED, true, "success", String.format("Add new category group %s successfully!", categoryGroup.getName()));

  }

  @Override
  public ResponseEntity<?> addCategory(Category category, Long groupId) {

    Map<String, String> messages = new HashMap<>();

    boolean existed = categoryRepository.existsById(category.getId());
    if (existed) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, false, "error", "Category ID is already taken.");
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, true, "error", String.format("Category name %s is already existed.", category.getName()));
    }

    boolean existedGroupId = categoryGroupRepository.existsById(groupId);
    if (!existedGroupId) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category group is not found.");
    }

    categoryRepository.save(category);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.CREATED, true, "success", String.format("Add new category %s successfully!", category.getName()));
  }

  @Override
  public ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id) {

    Map<String, String> messages = new HashMap<>();
    
    boolean existedGroupId = categoryGroupRepository.existsById(id);
    if (!existedGroupId) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category group is not found.");
    }

    categoryGroupRepository.save(categoryGroup);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.OK, true, "success", String.format("Update category group %s successfully!", categoryGroup.getName()));
    
  }

  @Override
  public ResponseEntity<?> updateCategory(Category category, Long groupId, Long id) {
    
    Map<String, String> messages = new HashMap<>();
    
    boolean existed = categoryRepository.existsById(id);
    if (!existed) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category is not found.");
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, true, "error", String.format("Category name %s is already existed.", category.getName()));
    }

    boolean existedGroupId = categoryGroupRepository.existsById(groupId);
    if (!existedGroupId) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category group is not found.");
    }

    categoryRepository.save(category);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.OK, true, "success", String.format("Update category %s successfully!", category.getName()));

  }

  @Override
  public ResponseEntity<?> deleteCategory(Long id) {
    
    Map<String, String> messages = new HashMap<>();
    
    boolean existed = categoryRepository.existsById(id);
    if (!existed) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category is not found.");
    }

    categoryRepository.deleteById(id);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.OK, true, "success", String.format("Delete category %s successfully.", id));
  }

  @Override
  public ResponseEntity<?> deleteCategoryGroup(Long id) {
    
    Map<String, String> messages = new HashMap<>();
    
    boolean existedGroupId = categoryGroupRepository.existsById(id);
    if (!existedGroupId) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.NOT_FOUND, true, "error", "This category group is not found.");
    }

    boolean stillHaveCategory = categoryRepository.existsByGroupId(id);
    if (stillHaveCategory) {
      return customResponseEntity.generateResponseEntity(messages, HttpStatus.CONFLICT, true, "error", "There are some categories still in the group.");
    }

    categoryGroupRepository.deleteById(id);

    return customResponseEntity.generateResponseEntity(messages, HttpStatus.OK, true, "success", String.format("Delete category group %s successfully.", id));

  }
  
}
