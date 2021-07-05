package com.ecommerce.gut.service;

import java.util.Collection;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.springframework.http.ResponseEntity;

public interface CategoryService {

  Collection<CategoryGroup> getAllCategoryGroups();

  CategoryGroup getCategoryGroupById(Long groupId);

  ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup);

  ResponseEntity<?> addCategoryToGroup(Category category, Long groupId);

  ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id);

  ResponseEntity<?> updateCategory(Category category, Long id, Long groupId);

  ResponseEntity<?> deleteCategory(Long id);

  ResponseEntity<?> deleteCategoryGroup(Long id);

}
