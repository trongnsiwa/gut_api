package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Optional;

import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.CustomNotFoundException;
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
  private CategoryGroupRepository categoryGroupRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Override
  public List<CategoryGroup> getAllCategoryGroups() {
    return categoryGroupRepository.findAll();
  }

  @Override
  public CategoryGroup getCategoryGroupById(Long groupId) {
    return categoryGroupRepository.findById(groupId)
        .orElseThrow(() -> new CustomNotFoundException(String.format("Category group %d", groupId)));
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format("Category %d", id)));
  }

  @Override
  public ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup) {
    boolean existed = categoryGroupRepository.existsById(categoryGroup.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Group Id %d is already taken.", categoryGroup.getId()), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Group name %s is already existed.", categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    categoryGroupRepository.save(categoryGroup);

    return customResponseEntity.generateMessageResponseEntity(String.format("Add new category group %s successful!", categoryGroup.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> addCategoryToGroup(Category category, Long groupId) {
    boolean existed = categoryRepository.existsById(category.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category Id %d is already taken.", category.getId()),
          HttpStatus.CONFLICT);
    }

    Optional<CategoryGroup> group = categoryGroupRepository.findById(groupId);
    if (!group.isPresent()) {
      throw new CustomNotFoundException(String.format("Category group %d", groupId));
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format("Category name %s is already existed.", category.getName()),
          HttpStatus.CONFLICT);
    }

    var categoryGroup = group.get();
    category.setCategoryGroup(categoryGroup);
    categoryRepository.save(category);

    return customResponseEntity.generateMessageResponseEntity(
        String.format("Add new category %s successful!", category.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id) {
    Optional<CategoryGroup> oldCategoryGroup = categoryGroupRepository.findById(id);
    if (!oldCategoryGroup.isPresent()) {
      throw new CustomNotFoundException(String.format("Category group %d", id));
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Group name %s is already existed.", categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    var newCategory = oldCategoryGroup.get();
    newCategory.setName(categoryGroup.getName());
    
    categoryGroupRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format("Update category group %d successful!", id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> updateCategory(Category category, Long id, Long groupId) {
    Optional<Category> oldCategory = categoryRepository.findById(id);
    if (!oldCategory.isPresent()) {
      throw new CustomNotFoundException(String.format("Category %d", id));
    }

    boolean existedGroupId = categoryGroupRepository.existsById(groupId);
    if (!existedGroupId) {
      throw new CustomNotFoundException(String.format("Category group %d", groupId));
    }

    Long categoryGroupId = categoryRepository.getGroupIdbyId(id);

    if (!categoryGroupId.equals(groupId)) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category %d is not in group %d", id, groupId), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format("Category name %s is already existed.", category.getName()), HttpStatus.CONFLICT);
    }

    var newCategory = oldCategory.get();
    newCategory.setName(category.getName());

    categoryRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format("Update category %d successful!", id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteCategory(Long id) {
    boolean existed = categoryRepository.existsById(id);
    if (!existed) {
      throw new CustomNotFoundException(String.format("Category %d", id));
    }

    categoryRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(String.format("Delete category %d successful.", id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteCategoryGroup(Long id) {
    boolean existedGroupId = categoryGroupRepository.existsById(id);
    if (!existedGroupId) {
      throw new CustomNotFoundException(String.format("Category group %d.", id));
    }

    boolean stillHaveCategory = categoryRepository.existsByGroupId(id);
    if (stillHaveCategory) {
      return customResponseEntity.generateMessageResponseEntity("There are some categories still in the group.", HttpStatus.CONFLICT);
    }

    categoryGroupRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(String.format("Delete category group %d successful.", id), HttpStatus.OK);
  }
  
}
