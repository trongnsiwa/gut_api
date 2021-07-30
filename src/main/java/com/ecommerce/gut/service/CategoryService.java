package com.ecommerce.gut.service;

import java.util.List;

import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface CategoryService {

  List<Category> getAllParentCategories();

  List<Category> getAllChildCategories();

  List<Category> getParentCategoriesPerPage(Integer pageNum, Integer pageSize, String sortBy);

  List<Category> searchByName(Integer pageNum, Integer pageSize, String sortBy, String name);

  List<Category> searchByParentAndName(Long categoryId, Integer pageNum, Integer pageSize, String sortBy, String name);

  Category getParentCategoryById(Long parentId);

  Category getCategoryById(Long id);

  Long countParents();

  Long countByName(String name);

  Long countByParentAndName(Long categoryId, String name);

  boolean createParentCategory(Category parentCategory) throws CreateDataFailException, DuplicateDataException;

  boolean addCategoryToParent(Category category, Long parentId) throws CreateDataFailException, DataNotFoundException, DuplicateDataException;

  Category updateParentCategory(Category parentCategory, Long id) throws UpdateDataFailException, DataNotFoundException, DuplicateDataException;

  Category updateCategory(Category category, Long id, Long parentId) throws UpdateDataFailException, DataNotFoundException, DuplicateDataException;

  boolean deleteCategory(Long id) throws DeleteDataFailException, DataNotFoundException, RestrictDataException;

  boolean deleteParentCategory(Long id) throws DeleteDataFailException, DataNotFoundException, RestrictDataException;

}
