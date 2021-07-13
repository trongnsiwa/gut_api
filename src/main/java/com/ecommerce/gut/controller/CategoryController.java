package com.ecommerce.gut.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.ecommerce.gut.converters.CategoryConverter;
import com.ecommerce.gut.dto.CategoryDTO;
import com.ecommerce.gut.dto.CategoryGroupDTO;
import com.ecommerce.gut.dto.ErrorCode;
import com.ecommerce.gut.dto.ResponseDTO;
import com.ecommerce.gut.dto.SuccessCode;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;
import com.ecommerce.gut.service.CategoryService;

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
@RequestMapping("/category")
@Tag(name = "category")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryConverter converter;

    @Operation(summary = "Get category groups with their categories per page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found all category book even if empty",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = CategoryGroupDTO.class)),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
    })
    @GetMapping("/all")
    public List<CategoryGroupDTO> getAllCategoryGroups(
            @RequestParam("num") @Min(1) Integer pageNumber,
            @RequestParam("size") @Min(1) Integer pageSize,
            @RequestParam("sort") @NotNull @NotBlank String sortBy) {
        return categoryService.getCategoryGroupsPerPage(pageNumber, pageSize, sortBy).stream()
                .map(categoryGroup -> converter.convertCategoryGroupToDto(categoryGroup))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a category group with its categories by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category group",
                    content = @Content(
                            schema = @Schema(implementation = CategoryGroupDTO.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found category group",
                    content = @Content),
    })
    @GetMapping("/group/{id}")
    public ResponseEntity<ResponseDTO> getCategoryGroupById(
            @PathVariable("id") @Min(1) Long groupId) {
        ResponseDTO response = new ResponseDTO();
        try {
            CategoryGroup foundCategoryGroup = categoryService.getCategoryGroupById(groupId);
            CategoryGroupDTO responseCategoryGroup =
                    converter.convertCategoryGroupToDto(foundCategoryGroup);
            response.setData(responseCategoryGroup);
            response.setSuccessCode(SuccessCode.CATEGORY_GROUP_LOADED_SUCCESS);
        } catch (Exception e) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
            throw new DataNotFoundException(ErrorCode.ERR_CATEGORY_GROUP_NOT_FOUND);
        }

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Get a category by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the category",
                    content = @Content(
                            schema = @Schema(implementation = CategoryDTO.class),
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found category",
                    content = @Content),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getCategoryById(Long id) {
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

    @Operation(summary = "Add a category group",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category group object to be added"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add new category group successful",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Enter invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Enter invalid data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Group Id or name is already taken",
                    content = @Content),
    })
    @PostMapping("/group/add")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> createCategoryGroup(
            @Valid @RequestBody CategoryGroupDTO categoryGroupDTO) throws CreateDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            CategoryGroup categoryGroup = converter.convertCategoryGroupToEntity(categoryGroupDTO);
            boolean added = categoryService.createCategoryGroup(categoryGroup);
            if (added) {
                response.setData(null);
                response.setSuccessCode(SuccessCode.CATEGORY_GROUP_CREATED_SUCCESS);
            }
        } catch (Exception ex) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
            throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_CREATED_FAIL);
        }
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Add a category to a group",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category object to be added"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add new category successful",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Need to provide group Id and enter valid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Category group is not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Category Id or name is already taken",
                    content = @Content),
    })
    @PostMapping("/group/{groupId}/add")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> addCategoryToGroup(
            @Valid @RequestBody CategoryDTO categoryDTO,
            @PathVariable("groupId") @Min(1) Long groupId) throws CreateDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            Category category = converter.convertCategoryToEntity(categoryDTO);
            boolean added = categoryService.addCategoryToGroup(category, groupId);
            if (added) {
                response.setData(null);
                response.setSuccessCode(SuccessCode.CATEGORY_CREATED_SUCCESS);
            }
        } catch (Exception e) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_GROUP_CREATED_FAIL);
            throw new CreateDataFailException(ErrorCode.ERR_CATEGORY_GROUP_CREATED_FAIL);
        }

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Update a category group by its Id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category group object to be updated"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update category group successful",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Need to provide group Id and enter valid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Category group is not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Group name is already taken",
                    content = @Content),
    })
    @PutMapping("/group/update/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> updateCategoryGroup(
            @Valid @RequestBody CategoryGroupDTO categoryGroupDTO,
            @PathVariable("id") @Min(1) Long id) throws UpdateDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            CategoryGroup categoryGroup = converter.convertCategoryGroupToEntity(categoryGroupDTO);
            CategoryGroup updatedCategoryGroup =
                    categoryService.updateCategoryGroup(categoryGroup, id);
            response.setData(updatedCategoryGroup);
            response.setSuccessCode(SuccessCode.CATEGORY_GROUP_UPDATED_SUCCESS);
        } catch (Exception e) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_GROUP_UPDATED_FAIL);
            throw new UpdateDataFailException(ErrorCode.ERR_CATEGORY_GROUP_UPDATED_FAIL);
        }

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Update a category by its Id and group Id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category object to be updated"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update category successful",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Need to provide category Id, its group Id and enter valid data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Category or its group is not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Category name is already taken",
                    content = @Content),
    })
    @PutMapping("/group/{groupId}/update/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
            @PathVariable("id") @Min(1) Long id, @Min(1) Long groupId)
            throws UpdateDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            Category category = converter.convertCategoryToEntity(categoryDTO);
            Category updatedCategory = categoryService.updateCategory(category, id, groupId);
            response.setData(updatedCategory);
            response.setSuccessCode(SuccessCode.CATEGORY_GROUP_UPDATED_SUCCESS);
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
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable("id") @Min(1) Long id)
            throws DeleteDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            boolean deleted = categoryService.deleteCategory(id);
            if (deleted) {
                response.setData(null);
                response.setSuccessCode(SuccessCode.CATEGORY_GROUP_DELETED_SUCCESS);
            }
        } catch (Exception e) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
            throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_DELETED_FAIL);
        }

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Delete a category group by its Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete category group successful",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Need to provide group Id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Category group is not found",
                    content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "There are some categories still in the group",
                    content = @Content),
    })
    @DeleteMapping("/group/delete/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategoryGroup(@PathVariable("id") @Min(1) Long id) throws DeleteDataFailException {
        ResponseDTO response = new ResponseDTO();
        try {
            boolean deleted = categoryService.deleteCategoryGroup(id);
            if (deleted) {
                response.setData(null);
                response.setSuccessCode(SuccessCode.CATEGORY_GROUP_DELETED_SUCCESS);
            }
        } catch (Exception e) {
            response.setErrorCode(ErrorCode.ERR_CATEGORY_GROUP_DELETED_FAIL);
            throw new DeleteDataFailException(ErrorCode.ERR_CATEGORY_GROUP_DELETED_FAIL);
        }

        return ResponseEntity.ok().body(response);
    }

}
