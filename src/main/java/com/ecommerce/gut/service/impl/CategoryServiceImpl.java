package com.ecommerce.gut.service.impl;

import static com.ecommerce.gut.specification.CategorySpecification.isNotDeleted;
import static com.ecommerce.gut.specification.CategorySpecification.nameContainsIgnoreCase;
import static com.ecommerce.gut.specification.CategorySpecification.nameEquals;
import static com.ecommerce.gut.specification.CategorySpecification.parentEquals;
import static com.ecommerce.gut.specification.CategorySpecification.parentIsNotNull;
import static com.ecommerce.gut.specification.CategorySpecification.parentIsNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.CategoryService;
import com.ecommerce.gut.specification.ProductSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductRepository productRepository;

  @Override
  public List<Category> getAllParentCategories() {
    return categoryRepository.findAll(Specification.where(parentIsNull()).and(isNotDeleted()));
  }

  @Override
  public List<Category> getAllChildCategories() {
    return categoryRepository.findAll(Specification.where(parentIsNotNull()).and(isNotDeleted()));
  }

  @Override
  public List<Category> getParentCategoriesPerPage(Integer pageNum, Integer pageSize,
      String sortBy) {
    Sort sort = null;

    if ("Z-A".equals(sortBy)) {
      sort = Sort.by("name").descending();
    } else {
      sort = Sort.by("name").ascending();
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    Specification<Category> parentSpec = parentIsNull();
    Specification<Category> isNotDeletedSpec = isNotDeleted();

    return categoryRepository.findAll(Specification.where(parentSpec).and(isNotDeletedSpec), pageRequest).getContent();
  }

  @Override
  public List<Category> searchByName(Integer pageNum, Integer pageSize, String sortBy,
      String name) {
    Sort sort = null;

    if ("Z-A".equals(sortBy)) {
      sort = Sort.by("name").descending();
    } else {
      sort = Sort.by("name").ascending();
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    Specification<Category> searchSpec = nameContainsIgnoreCase(name);
    Specification<Category> isNotDeletedSpec = isNotDeleted();

    return categoryRepository.findAll(Specification.where(searchSpec).and(isNotDeletedSpec), pageRequest).getContent();
  }

  @Override
  public List<Category> searchByParentAndName(Long parentId, Integer pageNum, Integer pageSize, String sortBy,
      String name) {
    Category parent = categoryRepository.findById(parentId).get();

    Sort sort = null;

    if ("Z-A".equals(sortBy)) {
      sort = Sort.by("name").descending();
    } else {
      sort = Sort.by("name").ascending();
    }

    PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

    Specification<Category> searchSpec = nameContainsIgnoreCase(name);
    Specification<Category> isNotDeletedSpec = isNotDeleted();

    return categoryRepository.findAll(Specification.where(searchSpec).and(parentEquals(parent)).and(isNotDeletedSpec), pageRequest).getContent();
  }

  @Override
  public Long countParents() {
    return categoryRepository.count(Specification.where(parentIsNull()).and(isNotDeleted()));
  }

  @Override
  public Long countByName(String name) {
    return categoryRepository.count(Specification.where(nameContainsIgnoreCase(name)).and(isNotDeleted()));
  }

  @Override
  public Long countByParentAndName(Long parentId, String name) {
    Category parent = categoryRepository.findById(parentId).get();

    return categoryRepository.count(Specification.where(parentEquals(parent)).and(nameContainsIgnoreCase(name)).and(isNotDeleted()));
  }

  @Override
  public Category getParentCategoryById(Long parentId) {
    return categoryRepository.findById(parentId)
        .orElseThrow(() -> {
          LOGGER.info("Category parent {} is not found", parentId);
          return new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
        });
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> {
          LOGGER.info("Category {} is not found", id);
          return new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        });
  }


  @Override
  public boolean createParentCategory(Category parentCategory)
      throws CreateDataFailException, DuplicateDataException {
    try {
      boolean isUniqueName = categoryRepository.existsByName(parentCategory.getName());

      if (isUniqueName) {
        LOGGER.info("Category parent name {} is already existed", parentCategory.getName());
        throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
      }

      parentCategory.setDeleted(false);
      categoryRepository.save(parentCategory);
    } catch (DuplicateDataException ex) {
      throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
    } catch (Exception ex) {
      LOGGER.info("Fail to create category parent {}", parentCategory.getName());
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_PARENT_CREATED_FAIL);
    }

    return true;
  }


  @Override
  public boolean addCategoryToParent(Category category, Long parentId)
      throws CreateDataFailException, DataNotFoundException, DuplicateDataException {
    try {
      Optional<Category> parent = categoryRepository.findById(parentId);

      if (!parent.isPresent()) {
        LOGGER.info("Category parent {} is not found", parentId);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      }

      boolean isUniqueName = categoryRepository.existsByName(category.getName());

      if (isUniqueName) {
        LOGGER.info("Category name {} is already existed", category.getName());
        throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      }
      category.setDeleted(false);
      category.setParent(parent.get());

      categoryRepository.save(category);
    } catch (DuplicateDataException ex) {
      throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception ex) {
      LOGGER.info("Fail to add category {} to parent {}", category.getName(), parentId);
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
    }

    return true;
  }


  @Override
  public Category updateParentCategory(Category parentCategory, Long id)
      throws UpdateDataFailException, DataNotFoundException, DuplicateDataException {
    try {
      Optional<Category> oldCategoryparent = categoryRepository.findById(id);

      if (!oldCategoryparent.isPresent()) {
        LOGGER.info("Category parent {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      }

      Optional<Category> categoryWithName = categoryRepository.findOne(nameEquals(parentCategory.getName()));

      if (categoryWithName.isPresent() && !categoryWithName.get().getId().equals(id)) {
          LOGGER.info("Category parent name {} is already existed", parentCategory.getName());
          throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
      }

      var newCategory = oldCategoryparent.get();
      newCategory.setName(parentCategory.getName());
      newCategory.setParent(null);

      return categoryRepository.save(newCategory);
    } catch (DuplicateDataException ex) {
      throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception ex) {
      LOGGER.info("Fail to update category parent {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_PARENT_UPDATED_FAIL);
    }
  }


  @Override
  public Category updateCategory(Category category, Long id, Long parentId)
      throws UpdateDataFailException, DataNotFoundException, DuplicateDataException {
    try {
      Optional<Category> oldCategory = categoryRepository.findById(id);

      if (!oldCategory.isPresent()) {
        LOGGER.info("Category {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      }

      Optional<Category> existedParent = Optional.empty();

      if (!Objects.isNull(parentId)) {
        existedParent = categoryRepository.findById(parentId);

        if (!existedParent.isPresent()) {
          LOGGER.info("Category parent {} is not found", parentId);
          throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
        }
      }

      Optional<Category> categoryWithName = categoryRepository.findOne(nameEquals(category.getName()));

      if (categoryWithName.isPresent() && !id.equals(categoryWithName.get().getId())) {
        LOGGER.info("Category name {} is already existed", category.getName());
        throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      }

      var newCategory = oldCategory.get();
      newCategory.setName(category.getName());

      if (!Objects.isNull(parentId)) {
        newCategory.setParent(existedParent.get());
      } else {
        newCategory.setParent(null);
      }

      return categoryRepository.save(newCategory);
    } catch (DuplicateDataException ex) {
      throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
    } catch (DataNotFoundException ex) {

      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_CATEGORY_NOT_FOUND)) {
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      } else {
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      }

    } catch (Exception ex) {
      LOGGER.info("Fail to update category {}", id);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_UPDATED_FAIL);
    }
  }


  @Override
  public boolean deleteCategory(Long id) throws DeleteDataFailException, DataNotFoundException, RestrictDataException {
    try {
      Category existedCategory = categoryRepository.findById(id).orElseThrow(() -> {
        LOGGER.info("Category {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      });

      Specification<Product> categoryEqualsSpec = ProductSpecification.categoryEquals(existedCategory);

      long totalProducts = productRepository.count(categoryEqualsSpec);

      if (totalProducts > 0) {
        LOGGER.info("Category {} still have products", id);
        throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_IN_CATEGORY);
      }

      existedCategory.setDeleted(true);
      categoryRepository.save(existedCategory);
    } catch (DataNotFoundException ex) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    } catch (RestrictDataException ex) {
      throw new RestrictDataException(ErrorCode.ERR_PRODUCT_STILL_IN_CATEGORY);
    } catch (Exception ex) {
      LOGGER.info("Fail to delete category {}", id);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
    }

    return true;
  }

  @Override
  public boolean deleteParentCategory(Long id)
      throws DeleteDataFailException, DataNotFoundException, RestrictDataException {
    try {
      Category existedParent= categoryRepository.findById(id).orElseThrow(() -> {
        LOGGER.info("Category parent {} is not found", id);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      });

      boolean stillHaveCategory = categoryRepository.existsByParentId(id);
      
      if (stillHaveCategory) {
        LOGGER.info("Category parent {} still have categories", id);
        throw new RestrictDataException(ErrorCode.ERR_CATEGORY_STILL_IN_PARENT);
      }

      existedParent.setDeleted(true);
      categoryRepository.save(existedParent);
    } catch (RestrictDataException e) {
      throw new RestrictDataException(ErrorCode.ERR_CATEGORY_STILL_IN_PARENT);
    } catch (DataNotFoundException e) {
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception e) {
      LOGGER.info("Fail to delete category parent {}", id);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_PARENT_DELETED_FAIL);
    }

    return true;
  }

}
