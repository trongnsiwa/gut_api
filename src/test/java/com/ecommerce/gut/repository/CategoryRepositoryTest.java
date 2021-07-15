package com.ecommerce.gut.repository;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import com.ecommerce.gut.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  public void testFindCategoryParentByIdSuccess() throws Exception {
    Category foundCategoryGroup = categoryRepository.findById(1L).get();

    assertNotNull(foundCategoryGroup);
    assertEquals("",foundCategoryGroup.getName());
  }

  @Test
  public void testFindAllPerPageSuccess()  throws Exception {
    String parentName = "Test2";
    PageRequest pageRequest= PageRequest.of(0, 2, Sort.by("name").descending());

    List<Category> foundCategoryParents = categoryRepository.getParentCategoryPerPage(pageRequest).getContent();

    assertEquals(2, foundCategoryParents.size());
    assertEquals(foundCategoryParents.get(0).getName(), parentName);
  }

  @Test
  public void testCreateCategoryParentSuccess()  throws Exception{
    Category categoryParent = new Category(0L, "Test2");
    categoryParent.setParent(null);
    assertNotNull(categoryRepository.save(categoryParent));
  }

  @Test
  public void testExistedByNameSuccess()  throws Exception{
    String name = "Test";
    assertEquals(true, categoryRepository.existsByName(name));
  }

  @Test
  public void testAddCategoryToGroupSuccess() throws Exception {
    Category category = new Category();
    category.setName("Cate Test");
    Category categoryParent = categoryRepository.findById(158L).get();
    category.setParent(categoryParent);

    assertNotNull(categoryRepository.save(category));
  }

  @Test
  public void testGetParentByIdSuccess() throws Exception {
    Long parentId = categoryRepository.getParentIdbyId(159L);
    Category parent = categoryRepository.findById(parentId).get();
    assertNotNull(parent);
    assertEquals(2, parent.getSubCategories().size());
  }

  @Test
  public void testUpdateCategoryParentSuccess() throws Exception {
    Category category = categoryRepository.findById(159L).get();
    Category parent = categoryRepository.findById(157L).get();
    category.setParent(parent);
    assertNotNull(categoryRepository.save(category));
  }

  @Test
  public void testDeleteCategorySuccess() throws Exception {
    categoryRepository.deleteById(162L);
    assertEquals(true, !categoryRepository.findById(162L).isPresent());
  }

  @Test
  public void testDeleteCategoryParentIfNotChildSuccess() throws Exception {
    categoryRepository.deleteById(157L);
    assertEquals(true, !categoryRepository.findById(157L).isPresent());
  }

  @Test
  public void testDeleteCategoryParentIfChildSuccess() throws Exception {
    Category parent = categoryRepository.findById(158L).get();
    parent.getSubCategories().stream()
        .forEach((sub) -> {
          sub.setParent(null);
          categoryRepository.save(sub);
        });

    categoryRepository.deleteById(158L);
    assertEquals(true, !categoryRepository.findById(158L).isPresent());
  }

  @Test
  public void testDeleteCategoryParentIfChildFail() throws Exception {
    assertThrows(Exception.class, () -> categoryRepository.deleteById(158L));
  }

}
