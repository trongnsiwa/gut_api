package com.ecommerce.gut.service;

import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.RestrictDataException;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @BeforeEach
  public void setUp() {
    Category parent1 = new Category(1L, "Parent 1");
    parent1.setParent(null);
    Category parent2 = new Category(2L, "AParent 2");
    parent2.setParent(null);
    List<Category> categories = Arrays.asList(parent1, parent2);

    Category child1 = new Category(3L, "Child 1");
    child1.setParent(parent1);
    parent1.getSubCategories().add(child1);

    Category child2 = new Category(4L, "Child 2");
    child2.setParent(parent1);
    parent1.getSubCategories().add(child2);

    // Page<Category> pages = new PageImpl<>(categories);
    // Mockito.when(this.categoryRepository.getParentCategoryPerPage(any(Pageable.class))).thenReturn(pages);

    Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(any(Category.class));

    Mockito.when(categoryRepository.findById(child1.getId())).thenReturn(Optional.of(child1));
    Mockito.when(categoryRepository.findById(parent1.getId())).thenReturn(Optional.of(parent1));
    Mockito.when(categoryRepository.existsById(parent1.getId())).thenReturn(true);
    Mockito.when(categoryRepository.existsByParentId(parent1.getId())).thenReturn(true);

  }

  @Test
  public void testGetParentCategoriesPerPageSuccess() throws LoadDataFailException {
    List<Category> categories = categoryService.getParentCategoriesPerPage(1, 2, "Z-A");
    assertEquals(2, categories.size());
    assertEquals("Parent 1", categories.get(0).getName());
  }

  @Test
  public void testGetParentCategoryByIdSuccess() {
    Category foundCategory = categoryService.getCategoryById(1L);
    assertEquals(2, foundCategory.getSubCategories().size());
    assertNull(foundCategory.getParent());
  }

  @Test
  public void testGetCategoryByIdSuccess() {
    Category foundCategory = categoryService.getCategoryById(3L);
    assertEquals(1L, foundCategory.getParent().getId());
  }

  @Test
  public void createDuplicateNameCategorySuccess() {

    String name = "Parent 1";
    Category parentCategory = new Category(5l, "Parent 1");
    Mockito.when(categoryRepository.existsByName(name)).thenReturn(true);

    assertThrows(DuplicateDataException.class, () -> categoryService.createParentCategory(parentCategory));
  }

  @Test
  public void testDeleteParentCategoryFail() throws Exception{
    assertThrows(RestrictDataException.class, () -> categoryService.deleteParentCategory(1L));
  }

}
