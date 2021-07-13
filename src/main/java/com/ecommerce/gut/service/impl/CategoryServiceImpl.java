package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Optional;
import com.ecommerce.gut.dto.ErrorCode;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.repository.CategoryGroupRepository;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

  @Autowired
  private CategoryGroupRepository categoryGroupRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Override
  public List<CategoryGroup> getCategoryGroupsPerPage(Integer pageNum, Integer pageSize,
      String sortBy) {
    Sort sort = null;
    if ("Z-A".equals(sortBy)) {
      sort = Sort.by("name").descending();
    } else {
      sort = Sort.by("name");
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
    return categoryGroupRepository.findAll(pageRequest).getContent();
  }

  @Override
  public CategoryGroup getCategoryGroupById(Long groupId) {
    return categoryGroupRepository.findById(groupId)
        .orElseThrow(() -> {
          LOGGER.info("Category group %d is not found", groupId);
          return new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
        });
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("Category %d is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        });
  }

  @Override
  public boolean createCategoryGroup(CategoryGroup categoryGroup) throws CreateDataFailException {
    try {
      boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
      if (isUniqueName) {
        LOGGER.info("Category group name %s is already existed", categoryGroup.getName());
        throw new DuplicateDataException(ErrorCode.ERR_GROUP_NAME_EXISTED);
      }
  
      categoryGroupRepository.save(categoryGroup);
    } catch (Exception ex) {
      LOGGER.info("Fail to create category group %d", categoryGroup.getId());
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_GROUP_CREATED_FAIL);
    }
    
    return true;
  }

  @Override
  public boolean addCategoryToGroup(Category category, Long groupId) throws CreateDataFailException {
    try {
      Optional<CategoryGroup> group = categoryGroupRepository.findById(groupId);
      if (!group.isPresent()) {
        LOGGER.info("Category group %d is not found", groupId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
      }
  
      boolean isUniqueName = categoryRepository.existsByName(category.getName());
      if (isUniqueName) {
        LOGGER.info("Category name %s is already existed", category.getName());
        throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      }
  
      var categoryGroup = group.get();
      category.setCategoryGroup(categoryGroup);
      categoryRepository.save(category);
    } catch (Exception ex) {
      LOGGER.info("Fail to add category %d to group %d", category.getId(), groupId);
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
    }
    
    return true;
  }

  @Override
  public CategoryGroup updateCategoryGroup(CategoryGroup categoryGroup, Long id) throws UpdateDataFailException {
    try {
      Optional<CategoryGroup> oldCategoryGroup = categoryGroupRepository.findById(id);
      if (!oldCategoryGroup.isPresent()) {
        LOGGER.info("Category group %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
      }
  
      boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
      if (isUniqueName) {
        LOGGER.info("Category group name %s is already existed", categoryGroup.getName());
        throw new DuplicateDataException(ErrorCode.ERR_GROUP_NAME_EXISTED);
      }
  
      var newCategory = oldCategoryGroup.get();
      newCategory.setName(categoryGroup.getName());
      
      return categoryGroupRepository.save(newCategory);
    } catch (Exception ex) {
      LOGGER.info("Fail to update category group %d", id);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_GROUP_UPDATED_FAIL);
    }
  }

  @Override
  public Category updateCategory(Category category, Long id, Long groupId) throws UpdateDataFailException {
    try {
      Optional<Category> oldCategory = categoryRepository.findById(id);
      if (!oldCategory.isPresent()) {
        LOGGER.info("Category %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }
  
      boolean existedGroupId = categoryGroupRepository.existsById(groupId);
      if (!existedGroupId) {
        LOGGER.info("Category group %d is not found", groupId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
      }
  
      Long categoryGroupId = categoryRepository.getGroupIdbyId(id);
  
      if (!categoryGroupId.equals(groupId)) {
        LOGGER.info("Category %d is not in group %d", id, groupId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_IN_GROUP);
      }
  
      boolean isUniqueName = categoryRepository.existsByName(category.getName());
      if (isUniqueName) {
        LOGGER.info("Category name %s is already existed", category.getName());
        throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      }
  
      var newCategory = oldCategory.get();
      newCategory.setName(category.getName());
  
      return categoryRepository.save(newCategory);
    } catch (Exception ex) {
      LOGGER.info("Fail to update category %d", id);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_UPDATED_FAIL);
    }
  }

  @Override
  public boolean deleteCategory(Long id) throws DeleteDataFailException {
    try {
      boolean existed = categoryRepository.existsById(id);
      if (!existed) {
        LOGGER.info("Category %d is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }
  
      categoryRepository.deleteById(id);
    } catch (Exception ex) {
      LOGGER.info("Fail to delete category %d", id);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
    }

    return true;
  }

  @Override
  public boolean deleteCategoryGroup(Long id) throws DeleteDataFailException {
    try {
      boolean existedGroupId = categoryGroupRepository.existsById(id);
    if (!existedGroupId) {
      LOGGER.info("Category group %d is not found", id);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
    }

    boolean stillHaveCategory = categoryRepository.existsByGroupId(id);
    if (stillHaveCategory) {
      LOGGER.info("Category group %d still have categories", id);
      throw new RestrictDataException(ErrorCode.ERR_CATEGORY_STILL_IN_GROUP);
    }

    categoryGroupRepository.deleteById(id);
    } catch (Exception e) {
      LOGGER.info("Fail to delete category group %d", id);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_GROUP_DELETED_FAIL);
    }
    
    return true;
  }
  
}
