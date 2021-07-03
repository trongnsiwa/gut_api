package com.ecommerce.gut.controller;

import java.util.Collection;
import java.util.Optional;
import javax.validation.Valid;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/category")
@Tag(name = "category")
public class CategoryController {

  @Autowired
  CategoryService categoryService;

  @Operation(summary = "Get all category groups with their categories")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all category book even if empty",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = CategoryGroup.class)),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/all")
  public Collection<CategoryGroup> getAllCategoryGroups() {
    return categoryService.getAllCategoryGroups();
  }

  @Operation(summary = "Get a category group with its categories by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the category group",
          content = @Content(
              schema = @Schema(implementation = CategoryGroup.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found category group", content = @Content),
  })
  @GetMapping("/group/{id}")
  public CategoryGroup getCategoryGroupById(@PathVariable("id") Long groupId) {
    return categoryService.getCategoryGroupById(groupId);
  }

  @Operation(summary = "Add a category group")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category group successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "409", description = "Group Id or name is already taken", content = @Content),
  })
  @PostMapping("/group/add")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addCategoryGroup(@Valid @RequestBody CategoryGroup categoryGroup) {
    return categoryService.addCategoryGroup(categoryGroup);
  }

  @Operation(summary = "Add a category to a group")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Category Id or name is already taken", content = @Content),
  })
  @PostMapping("/group/{groupId}/add")
  public ResponseEntity<?> addCategoryToGroup(@Valid @RequestBody Category category,
      @PathVariable("groupId") Optional<Long> groupId) {
    return categoryService.addCategoryToGroup(category, groupId);
  }

  @Operation(summary = "Update a category group by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category group successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Group name is already taken", content = @Content),
  })
  @PutMapping("/group/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateCategoryGroup(@Valid @RequestBody CategoryGroup categoryGroup,
      @PathVariable("id") Optional<Long> id) {
    return categoryService.updateCategoryGroup(categoryGroup, id);
  }

  @Operation(summary = "Update a category by its Id and group Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide category Id, its group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category or its group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Category name is already taken", content = @Content),
  })
  @PutMapping("/group/{groupId}/update/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category,
      @PathVariable("id") Optional<Long> id, Optional<Long> groupId) {
    return categoryService.updateCategory(category, id, groupId);
  }

  @Operation(summary = "Delete a category by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete category successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide category Id", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") Optional<Long> id) {
    return categoryService.deleteCategory(id);
  }

  @Operation(summary = "Delete a category group by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete category group successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide group Id", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category group is not found", content = @Content),
      @ApiResponse(responseCode = "409",
          description = "There are some categories still in the group", content = @Content),
  })
  @DeleteMapping("/group/delete/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategoryGroup(@PathVariable("id") Optional<Long> id) {
    return categoryService.deleteCategoryGroup(id);
  }

}
