package com.ecommerce.gut.service;

import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.springframework.http.ResponseEntity;

public interface CategoryService {

  List<CategoryGroup> getCategoryGroupsPerPage(Integer pageNum, Integer pageSize, String sortBy);

  CategoryGroup getCategoryGroupById(Long groupId);

  Category getCategoryById(Long id);

  ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup);

  ResponseEntity<?> addCategoryToGroup(Category category, Long groupId);

  ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id);

  ResponseEntity<?> updateCategory(Category category, Long id, Long groupId);

  ResponseEntity<?> deleteCategory(Long id);

  ResponseEntity<?> deleteCategoryGroup(Long id);

}
