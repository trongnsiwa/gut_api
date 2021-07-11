package com.ecommerce.gut.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.repository.CategoryGroupRepository;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.service.CategoryService;
import com.ecommerce.gut.util.CustomResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryGroupRepository categoryGroupRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CustomResponseEntity customResponseEntity;

  @Autowired
  private MessageSource messages;

  @Autowired
  private HttpServletRequest request;

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
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("category.message.cateGroupNotFound", null, request.getLocale()), groupId)));
  }

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id)
        .orElseThrow(() -> new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, request.getLocale()), id)));
  }

  @Override
  public ResponseEntity<?> addCategoryGroup(CategoryGroup categoryGroup) {

    Locale locale = request.getLocale();

    boolean existed = categoryGroupRepository.existsById(categoryGroup.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateGroupTaken", null, locale), categoryGroup.getId()), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.groupNameExisted", null, locale), categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    categoryGroupRepository.save(categoryGroup);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateGroupAddSucc", null, locale), categoryGroup.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> addCategoryToGroup(Category category, Long groupId) {

    Locale locale = request.getLocale();

    boolean existed = categoryRepository.existsById(category.getId());
    if (existed) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateTaken", null, locale), category.getId()),
          HttpStatus.CONFLICT);
    }

    Optional<CategoryGroup> group = categoryGroupRepository.findById(groupId);
    if (!group.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, locale), groupId));
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(
          String.format(messages.getMessage("category.message.cateNameExisted", null, locale), category.getName()),
          HttpStatus.CONFLICT);
    }

    var categoryGroup = group.get();
    category.setCategoryGroup(categoryGroup);
    categoryRepository.save(category);

    return customResponseEntity.generateMessageResponseEntity(
        String.format(messages.getMessage("category.message.cateAddSucc", null, locale), category.getName()), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<?> updateCategoryGroup(CategoryGroup categoryGroup, Long id) {

    Locale locale = request.getLocale();

    Optional<CategoryGroup> oldCategoryGroup = categoryGroupRepository.findById(id);
    if (!oldCategoryGroup.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateGroupNotFound", null, locale), id));
    }

    boolean isUniqueName = categoryGroupRepository.existsByName(categoryGroup.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.groupNameExisted", null, locale), categoryGroup.getName()), HttpStatus.CONFLICT);
    }

    var newCategory = oldCategoryGroup.get();
    newCategory.setName(categoryGroup.getName());
    
    categoryGroupRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateGroupUpdateSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> updateCategory(Category category, Long id, Long groupId) {

    Locale locale = request.getLocale();

    Optional<Category> oldCategory = categoryRepository.findById(id);
    if (!oldCategory.isPresent()) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, locale), id));
    }

    boolean existedGroupId = categoryGroupRepository.existsById(groupId);
    if (!existedGroupId) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateGroupNotFound", null, locale), groupId));
    }

    Long categoryGroupId = categoryRepository.getGroupIdbyId(id);

    if (!categoryGroupId.equals(groupId)) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateNotInGroup", null, locale), id, groupId), HttpStatus.CONFLICT);
    }

    boolean isUniqueName = categoryRepository.existsByName(category.getName());
    if (isUniqueName) {
      return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateNameExisted", null, locale), category.getName()), HttpStatus.CONFLICT);
    }

    var newCategory = oldCategory.get();
    newCategory.setName(category.getName());

    categoryRepository.save(newCategory);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateUpdateSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteCategory(Long id) {

    Locale locale = request.getLocale();

    boolean existed = categoryRepository.existsById(id);
    if (!existed) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateNotFound", null, locale), id));
    }

    categoryRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateDelSucc", null, locale), id), HttpStatus.OK);
  }

  @Override
  public ResponseEntity<?> deleteCategoryGroup(Long id) {

    Locale locale = request.getLocale();

    boolean existedGroupId = categoryGroupRepository.existsById(id);
    if (!existedGroupId) {
      throw new CustomNotFoundException(String.format(messages.getMessage("category.message.cateGroupNotFound", null, locale), id));
    }

    boolean stillHaveCategory = categoryRepository.existsByGroupId(id);
    if (stillHaveCategory) {
      return customResponseEntity.generateMessageResponseEntity(messages.getMessage("category.message.cateStillInGroup", null, locale), HttpStatus.CONFLICT);
    }

    categoryGroupRepository.deleteById(id);

    return customResponseEntity.generateMessageResponseEntity(String.format(messages.getMessage("category.message.cateGroupDelSucc", null, locale), id), HttpStatus.OK);
  }
  
}
