package com.ecommerce.gut.service;

import java.util.Collection;
import java.util.Optional;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
  
  Collection<CategoryGroup> getAllCategoryGroups();

  CategoryGroup getCategoryGroupById(Long groupId);

  ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup);

  ResponseEntity<?> addCategoryToGroup(Category category, Optional<Long> groupId);

  ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Optional<Long> id);

  ResponseEntity<?> updateCategory(Category category, Optional<Long> id, Optional<Long> groupId);

  ResponseEntity<?> deleteCategory(Optional<Long> id);

  ResponseEntity<?> deleteCategoryGroup(Optional<Long> id);

}
