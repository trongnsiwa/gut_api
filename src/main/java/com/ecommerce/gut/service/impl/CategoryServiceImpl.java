package com.ecommerce.gut.service.impl;

import java.util.Collection;
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
    return categoryGroupRepository.findById(groupId).orElseThrow(() -> new CustomNotFoundException("Category group"));
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
  public ResponseEntity<?> addCategoryToGroup(Category category, Long groupId) {
    boolean existed = categoryRepository.existsById(category.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity("Category Id is already taken.",
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

    CategoryGroup categoryGroup = group.get();
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

    CategoryGroup newCategory = oldCategoryGroup.get();
    newCategory.setName(categoryGroup.getName());
    
    categoryGroupRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity( String.format("Update category group %d successful!", id), HttpStatus.OK);
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

    Category newCategory = oldCategory.get();
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
