package com.ecommerce.gut.service;

import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
  
  List<CategoryGroup> getAllCategoryGroups();

  CategoryGroup getCategoryGroupById(Long groupId);

  ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup);

  ResponseEntity<?> addCategory(Category category, Long groupId);

  ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id);

  ResponseEntity<?> updateCategory(Category category, Long groupdId, Long id);

  ResponseEntity<?> deleteCategory(Long id);

  ResponseEntity<?> deleteCategoryGroup(Long id);

}
