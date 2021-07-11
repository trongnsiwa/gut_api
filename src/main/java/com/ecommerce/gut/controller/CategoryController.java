package com.ecommerce.gut.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.dto.CategoryDTO;
import com.ecommerce.gut.dto.CategoryGroupDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.service.CategoryService;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Validated
public class CategoryController {

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ModelMapper modelMapper;

  @Operation(summary = "Getcategory groups with their categories per page")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found all category book even if empty",
          content = @Content(
              array = @ArraySchema(schema = @Schema(implementation = CategoryGroupDTO.class)),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
  })
  @GetMapping("/all")
  public List<CategoryGroupDTO> getAllCategoryGroups(@RequestParam("num") @Min(1) Integer pageNumber,
  @RequestParam("size") @Min(1) Integer pageSize, @RequestParam("sort") @NotNull @NotBlank String sortBy) {
    return categoryService.getCategoryGroupsPerPage(pageNumber, pageSize, sortBy).stream()
        .map(this::convertCategoryGroupToDto)
        .collect(Collectors.toList());
  }

  @Operation(summary = "Get a category group with its categories by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the category group",
          content = @Content(
              schema = @Schema(implementation = CategoryGroupDTO.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found category group", content = @Content),
  })
  @GetMapping("/group/{id}")
  public CategoryGroupDTO getCategoryGroupById(@PathVariable("id") @Min(1) Long groupId) {
    return convertCategoryGroupToDto(categoryService.getCategoryGroupById(groupId));
  }

  @Operation(summary = "Get a category by its id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Found the category",
          content = @Content(
              schema = @Schema(implementation = CategoryDTO.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found category", content = @Content),
  })
  @GetMapping("/{id}")
  public CategoryDTO getCategoryById(Long id) {
    return convertCategoryToDto(categoryService.getCategoryById(id));
  }

  @Operation(summary = "Add a category group",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category group object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category group successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Enter invalid data", content = @Content),
      @ApiResponse(responseCode = "409", description = "Group Id or name is already taken", content = @Content),
  })
  @PostMapping("/group/add")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addCategoryGroup(@Valid @RequestBody CategoryGroupDTO categoryGroupDTO) {
    CategoryGroup categoryGroup = convertCategoryGroupToEntity(categoryGroupDTO);
    return categoryService.addCategoryGroup(categoryGroup);
  }

  @Operation(summary = "Add a category to a group",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category object to be added"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Add new category successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Category Id or name is already taken", content = @Content),
  })
  @PostMapping("/group/{groupId}/add")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> addCategoryToGroup(@Valid @RequestBody CategoryDTO categoryDTO,
      @PathVariable("groupId") @Min(1) Long groupId) {
    Category category = convertCategoryToEntity(categoryDTO);
    return categoryService.addCategoryToGroup(category, groupId);
  }

  @Operation(summary = "Update a category group by its Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category group object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category group successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Group name is already taken", content = @Content),
  })
  @PutMapping("/group/update/{id}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateCategoryGroup(@Valid @RequestBody CategoryGroupDTO categoryGroupDTO,
      @PathVariable("id") @Min(1) Long id) {
    CategoryGroup categoryGroup = convertCategoryGroupToEntity(categoryGroupDTO);
    return categoryService.updateCategoryGroup(categoryGroup, id);
  }

  @Operation(summary = "Update a category by its Id and group Id",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Category object to be updated"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update category successful", content = @Content),
      @ApiResponse(responseCode = "400",
          description = "Need to provide category Id, its group Id and enter valid data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category or its group is not found", content = @Content),
      @ApiResponse(responseCode = "409", description = "Category name is already taken", content = @Content),
  })
  @PutMapping("/group/{groupId}/update/{id}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
      @PathVariable("id") @Min(1) Long id, @Min(1) Long groupId) {
    Category category = convertCategoryToEntity(categoryDTO);
    return categoryService.updateCategory(category, id, groupId);
  }

  @Operation(summary = "Delete a category by its Id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Delete category successful", content = @Content),
      @ApiResponse(responseCode = "400", description = "Need to provide category Id", content = @Content),
      @ApiResponse(responseCode = "404", description = "Category is not found", content = @Content),
  })
  @DeleteMapping("/delete/{id}")
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategory(@PathVariable("id") @Min(1) Long id) {
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
  // @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteCategoryGroup(@PathVariable("id") @Min(1) Long id) {
    return categoryService.deleteCategoryGroup(id);
  }

  private Category convertCategoryToEntity(CategoryDTO categoryDTO) {
    return modelMapper.map(categoryDTO, Category.class);
  }

  private CategoryDTO convertCategoryToDto(Category category) {
    return modelMapper.map(category, CategoryDTO.class);
  }

  private CategoryGroupDTO convertCategoryGroupToDto(CategoryGroup categoryGroup) {
    return modelMapper.map(categoryGroup, CategoryGroupDTO.class);
  }

  private CategoryGroup convertCategoryGroupToEntity(CategoryGroupDTO categoryGroupDTO) {
    return modelMapper.map(categoryGroupDTO, CategoryGroup.class);
  }
}
