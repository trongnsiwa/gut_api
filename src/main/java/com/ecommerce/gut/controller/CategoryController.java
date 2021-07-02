package com.ecommerce.gut.controller;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/category")
public class CategoryController {
  
  @Autowired
  CategoryService categoryService;

  @GetMapping("/all")
  public List<CategoryGroup> getAllCategoryGroups() {
    return categoryService.getAllCategoryGroups();
  }

  @GetMapping("/group/{id}")
  public CategoryGroup getCategoryGroupById(@PathVariable("id") Long groupId) {
    return categoryService.getCategoryGroupById(groupId);
  }

  @PostMapping("/group/add")
  public ResponseEntity<?> addCategoryGroup(@Valid @RequestBody CategoryGroup categoryGroup) {
    return categoryService.addCategoryGroup(categoryGroup);
  }

  @PostMapping("/group/{groupId}/add")
  public ResponseEntity<?> addCategoryToGroup(@Valid @RequestBody Category category, @PathVariable("groupId") Optional<Long> groupId) {
    return categoryService.addCategoryToGroup(category, groupId);
  }

  @PutMapping("/group/update/{id}")
  public ResponseEntity<?> updateCategoryGroup(@Valid @RequestBody CategoryGroup categoryGroup, @PathVariable("id") Optional<Long> id) {
    return categoryService.updateCategoryGroup(categoryGroup, id);
  }

  @PutMapping("/group/{groupId}/update/{id}")
  public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category, @PathVariable("id") Optional<Long> id, @PathVariable("groupId") Optional<Long> groupId) {
    return categoryService.updateCategory(category, id, groupId);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") Optional<Long> id) {
    return categoryService.deleteCategory(id);
  }

  @DeleteMapping("/group/delete/{id}")
  public ResponseEntity<?> deleteCategoryGroup(@PathVariable("id") Optional<Long> id) {
    return categoryService.deleteCategoryGroup(id);
  }

}
