package com.ecommerce.gut.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.converters.CategoryConverter;
import com.ecommerce.gut.dto.CategoryDTO;
import com.ecommerce.gut.dto.CategoryParentDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.payload.response.ResponseDTO;
import com.ecommerce.gut.payload.response.SuccessCode;
import com.ecommerce.gut.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/category")
@Tag(name = "category")
@Validated
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private CategoryConverter converter;

  @Operation(summary = "Get category parents with their categories per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Found all category book even if empty", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/all")
  public ResponseEntity<ResponseDTO> getAllCategoryParents(
      @RequestParam("num") @Min(1) Integer pageNumber,
      @RequestParam("size") @Min(1) Integer pageSize,
      @RequestParam("sort") @NotNull @NotBlank String sortBy) throws LoadDataFailException {
    ResponseDTO response = new ResponseDTO();
    try {
      List<CategoryParentDTO> categoryParents =
          categoryService.getParentCategoriesPerPage(pageNumber, pageSize, sortBy).stream()
              .map(categoryParent -> converter.convertCategoryParentToDto(categoryParent))
              .collect(Collectors.toList());

      response.setData(categoryParents);
      response.setSuccessCode(SuccessCode.CATEGORY_PARENT_LOADED_SUCCESS);
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_LOADED_FAIL);
      throw new LoadDataFailException(ErrorCode.ERR_CATEGORY_PARENT_LOADED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get a category parent with its categories by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the category parent",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found category parent",
          content = @Content),
  })
  @GetMapping("/parent/{id}")
  public ResponseEntity<ResponseDTO> getCategoryparentById(
      @PathVariable("id") @Min(1) Long parentId) throws DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category foundCategoryParent = categoryService.getParentCategoryById(parentId);
      CategoryParentDTO responseCategoryParent =
          converter.convertCategoryParentToDto(foundCategoryParent);
      response.setData(responseCategoryParent);
      response.setSuccessCode(SuccessCode.CATEGORY_PARENT_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Get a category by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the category", content = @Content),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found category",
          content = @Content),
  })
  @GetMapping("/{id}")
  public ResponseEntity<ResponseDTO> getCategoryById(@PathVariable("id") @Min(1) Long id) throws DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category foundCategory = categoryService.getCategoryById(id);
      CategoryDTO responseCategory = converter.convertCategoryToDto(foundCategory);
      response.setData(responseCategory);
      response.setSuccessCode(SuccessCode.CATEGORY_LOADED_SUCCESS);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    }
    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Add a category parent",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category parent object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category parent successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Data not found",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "parent Id or name is already taken",
          content = @Content),
  })
  @PostMapping("/parent/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> createCategoryparent(
      @Valid @RequestBody CategoryDTO parentDTO)
      throws CreateDataFailException, DuplicateDataException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category categoryParent = converter.convertCategoryToEntity(parentDTO);
      boolean added = categoryService.createParentCategory(categoryParent);
      if (added) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.CATEGORY_PARENT_CREATED_SUCCESS);
      }
    } catch (DuplicateDataException ex) {
      response.setErrorCode(ErrorCode.ERR_PARENT_NAME_EXISTED);
      throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
    } catch (Exception ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
    }
    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Add a category to a parent",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category successful",
          content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide parent Id and enter valid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Category parent is not found",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Category Id or name is already taken",
          content = @Content),
  })
  @PostMapping("/parent/{parentId}/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> addCategoryToparent(
      @Valid @RequestBody CategoryDTO categoryDTO,
      @PathVariable("parentId") @Min(1) Long parentId)
      throws CreateDataFailException, DuplicateDataException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category category = converter.convertCategoryToEntity(categoryDTO);
      boolean added = categoryService.addCategoryToParent(category, parentId);
      if (added) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.CATEGORY_CREATED_SUCCESS);
      }
    } catch (DuplicateDataException ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
    } catch (DataNotFoundException ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
      throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update a category parent by its Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category parent object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category parent successful",
          content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide parent Id and enter valid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Category parent is not found",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "parent name is already taken",
          content = @Content),
  })
  @PutMapping("/parent/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> updateCategoryParent(
      @Valid @RequestBody CategoryDTO parentDTO,
      @PathVariable("id") @Min(1) Long id)
      throws UpdateDataFailException, DuplicateDataException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category categoryParent = converter.convertCategoryToEntity(parentDTO);
      Category updatedCategoryParent =
          categoryService.updateParentCategory(categoryParent, id);
      CategoryParentDTO responseParent =
          converter.convertCategoryParentToDto(updatedCategoryParent);

      response.setData(responseParent);
      response.setSuccessCode(SuccessCode.CATEGORY_PARENT_UPDATED_SUCCESS);
    } catch (DuplicateDataException ex) {
      response.setErrorCode(ErrorCode.ERR_PARENT_NAME_EXISTED);
      throw new DuplicateDataException(ErrorCode.ERR_PARENT_NAME_EXISTED);
    } catch (DataNotFoundException ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_UPDATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_PARENT_UPDATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Update a category by its Id and parent Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category successful",
          content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide category Id, its parent Id and enter valid data",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Category or its parent is not found",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Category name is already taken",
          content = @Content),
  })
  @PutMapping("/parent/{parentId}/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
      @PathVariable("id") @Min(1) Long id, @PathVariable("parentId") @Min(1) Long parentId)
      throws UpdateDataFailException, DuplicateDataException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      Category category = converter.convertCategoryToEntity(categoryDTO);
      Category updatedCategory = categoryService.updateCategory(category, id, parentId);
      CategoryDTO responseCategory = converter.convertCategoryToDto(updatedCategory);

      response.setData(responseCategory);
      response.setSuccessCode(SuccessCode.CATEGORY_PARENT_UPDATED_SUCCESS);
    } catch (DuplicateDataException ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
      throw new DuplicateDataException(ErrorCode.ERR_CATEGORY_NAME_EXISTED);
    } catch (DataNotFoundException ex) {
      String message = ex.getMessage();

      if (message.equals(ErrorCode.ERR_CATEGORY_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      } else if (message.equals(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND)) {
        response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      } else {
        response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_IN_PARENT);
        throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_IN_PARENT);
      }

    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_UPDATED_FAIL);
      throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_UPDATED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Delete a category by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete category successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide category Id",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Category is not found",
          content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable("id") @Min(1) Long id)
      throws DeleteDataFailException, DataNotFoundException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean deleted = categoryService.deleteCategory(id);
      if (deleted) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.CATEGORY_PARENT_DELETED_SUCCESS);
      }
    } catch (DataNotFoundException ex) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

  @Operation(summary = "Delete a category parent by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete category parent successful",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide parent Id",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Category parent is not found",
          content = @Content),
      @ApiResponse(responseCode = "409",
          description = "There are some categories still in the parent",
          content = @Content),
  })
  @DeleteMapping("/parent/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategoryParent(@PathVariable("id") @Min(1) Long id)
      throws DeleteDataFailException, RestrictDataException {
    ResponseDTO response = new ResponseDTO();
    try {
      boolean deleted = categoryService.deleteParentCategory(id);
      if (deleted) {
        response.setData(null);
        response.setSuccessCode(SuccessCode.CATEGORY_PARENT_DELETED_SUCCESS);
      }
    } catch (RestrictDataException e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_STILL_IN_PARENT);
      throw new RestrictDataException(ErrorCode.ERR_CATEGORY_STILL_IN_PARENT);
    } catch (DataNotFoundException e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
      throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_PARENT_NOT_FOUND);
    } catch (Exception e) {
      response.setErrorCode(ErrorCode.ERR_CATEGORY_PARENT_DELETED_FAIL);
      throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_PARENT_DELETED_FAIL);
    }

    return ResponseEntity.ok().body(response);
  }

}
