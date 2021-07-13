package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryGroupRepositoryTest {

  @Autowired
  private CategoryGroupRepository categoryGroupRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  public void testFindCategoryGroupByIdSuccess() {
    CategoryGroup categoryGroup = new CategoryGroup(1L, "Outer");
    categoryGroupRepository.save(categoryGroup);

    CategoryGroup foundCategoryGroup = categoryGroupRepository.findById(categoryGroup.getId()).get();

    assertNotNull(foundCategoryGroup);
    assertEquals(categoryGroup.getName(), foundCategoryGroup.getName());
  }

  @Test
  public void testFindAllPerPageSuccess() {
    String groupName = "Tops";
    PageRequest pageRequest= PageRequest.of(0, 2, Sort.by("name").descending());

    List<CategoryGroup> foundCategoryGroups =  categoryGroupRepository.findAll(pageRequest).getContent();

    assertEquals(2, foundCategoryGroups.size());
    assertEquals(foundCategoryGroups.get(0).getName(), groupName);
  }

  @Test
  public void testCreateCategoryGroupSuccess() {
    CategoryGroup categoryGroup = new CategoryGroup(0L, "Test");
    assertNotNull(categoryGroupRepository.save(categoryGroup));
  }

  @Test
  public void testExistedByNameSuccess() {
    String name = "Test";

    assertEquals(true, categoryGroupRepository.existsByName(name));
  }

  @Test
  public void testAddCategoryToGroup() {
    Category category = new Category();
    category.setName("Cate Test");
    CategoryGroup categoryGroup = categoryGroupRepository.findById(5L).get();
    category.setCategoryGroup(categoryGroup);

    assertNotNull(categoryRepository.save(category));
  }

}
