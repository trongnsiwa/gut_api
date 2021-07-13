package com.ecommerce.gut.service;

import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface CategoryService {

  List<CategoryGroup> getCategoryGroupsPerPage(Integer pageNum, Integer pageSize, String sortBy);

  CategoryGroup getCategoryGroupById(Long groupId);

  Category getCategoryById(Long id);

  boolean createCategoryGroup(CategoryGroup categoryGroup) throws CreateDataFailException;

  boolean addCategoryToGroup(Category category, Long groupId) throws CreateDataFailException;

  CategoryGroup updateCategoryGroup(CategoryGroup categoryGroup, Long id) throws UpdateDataFailException;

  Category updateCategory(Category category, Long id, Long groupId) throws UpdateDataFailException;

  boolean deleteCategory(Long id) throws DeleteDataFailException;

  boolean deleteCategoryGroup(Long id) throws DeleteDataFailException;

}
