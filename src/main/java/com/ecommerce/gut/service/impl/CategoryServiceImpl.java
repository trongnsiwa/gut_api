package com.ecommerce.gut.service.impl;

import java.util.Collection;
import java.util.Optional;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.ItemNotFoundException;
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
  public Collection<CategoryGroup> getAllCategoryGroups() {
    return categoryGroupRepository.findAll();
  }

  @Override
  public CategoryGroup getCategoryGroupById(Long groupId) {

    return categoryGroupRepository.findById(groupId).orElseThrow(() -> new ItemNotFoundException("Category group"));

  }

  @Override
  public ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup) {

    boolean existed = categoryGroupRepository.existsById(categoryGroup.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity("Group Id is already taken.", HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Group name %s is already existed.", categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    categoryGroupRepository.save(categoryGroup);

    return customResponseEntity.generateMessageResponseEntity(String.format("Add new category group %s successful!", categoryGroup.getName()), HttpStatus.CREATED);

  }

  @Override
  public ResponseEntity<?> addCategoryToGroup(Category category, Optional<Long> groupId) {

    boolean existed = categoryRepository.existsById(category.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity("Category Id is already taken.",
          HttpStatus.CONFLICT);
    }

    if (!groupId.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide group Id.",
          HttpStatus.BAD_REQUEST);
    }

    Optional<CategoryGroup> group = categoryGroupRepository.findById(groupId.get());
    if (!group.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category group %d is not found.", groupId.get()),
          HttpStatus.NOT_FOUND);
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format("Category name %s is already existed.", category.getName()),
          HttpStatus.CONFLICT);
    }

    CategoryGroup categoryGroup = group.get();
    category.setCategoryGroup(categoryGroup);
    categoryRepository.save(category);

    return customResponseEntity.generateMessageResponseEntity(
        String.format("Add new category %s successful!", category.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Optional<Long> id) {
    
    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide group Id.", HttpStatus.BAD_REQUEST);
    }

    Optional<CategoryGroup> oldCategoryGroup = categoryGroupRepository.findById(id.get());
    if (!oldCategoryGroup.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category group %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Group name %s is already existed.", categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    CategoryGroup newCategory = oldCategoryGroup.get();
    newCategory.setName(categoryGroup.getName());
    
    categoryGroupRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity( String.format("Update category group %d successful!", id.get()), HttpStatus.OK);
    
  }

  @Override
  public ResponseEntity<?> updateCategory(Category category, Optional<Long> id, Optional<Long> groupId) {
    
    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide category Id.", HttpStatus.BAD_REQUEST);
    }

    Optional<Category> oldCategory = categoryRepository.findById(id.get());
    if (!oldCategory.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    if (!groupId.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide group Id.", HttpStatus.BAD_REQUEST);
    }

    boolean existedGroupId = categoryGroupRepository.existsById(groupId.get());
    if (!existedGroupId) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category group %d is not found.", groupId.get()), HttpStatus.NOT_FOUND);
    }

    Long categoryGroupId = categoryRepository.getGroupIdbyId(id.get());

    if (!categoryGroupId.equals(groupId.get())) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category %d is not in group %d", id.get(), groupId.get()), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category name %s is already existed.", category.getName()), HttpStatus.CONFLICT);
    }

    Category newCategory = oldCategory.get();
    newCategory.setName(category.getName());

    categoryRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format("Update category %d successful!", id.get()), HttpStatus.OK);

  }

  @Override
  public ResponseEntity<?> deleteCategory(Optional<Long> id) {
    
    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide category Id.", HttpStatus.BAD_REQUEST);
    }

    boolean existed = categoryRepository.existsById(id.get());
    if (!existed) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    categoryRepository.deleteById(id.get());

    return customResponseEntity.generateMessageResponseEntity(String.format("Delete category %d successful.", id.get()), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteCategoryGroup(Optional<Long> id) {

    if (!id.isPresent()) {
      return customResponseEntity.generateMessageResponseEntity("Please provide group Id.", HttpStatus.BAD_REQUEST);
    }
    
    boolean existedGroupId = categoryGroupRepository.existsById(id.get());
    if (!existedGroupId) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category group %d is not found.", id.get()), HttpStatus.NOT_FOUND);
    }

    boolean stillHaveCategory = categoryRepository.existsByGroupId(id.get());
    if (stillHaveCategory) {
      return customResponseEntity.generateMessageResponseEntity("There are some categories still in the group.", HttpStatus.CONFLICT);
    }

    categoryGroupRepository.deleteById(id.get());

    return customResponseEntity.generateMessageResponseEntity(String.format("Delete category group %d successful.", id.get()), HttpStatus.OK);

  }
  
}
