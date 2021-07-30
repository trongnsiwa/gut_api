package com.ecommerce.gut.specification;

import com.ecommerce.gut.entity.Category;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {
  
  private CategorySpecification() {}

  public static Specification<Category> nameEquals(String name) {
    return (root, query, cb) -> cb.equal(root.<String>get("name"), name);
  }

  public static Specification<Category> parentEquals(Category parent) {
    return (root, query, cb) ->  cb.equal(root.<String>get("parent"), parent);
  }

  public static Specification<Category> nameContainsIgnoreCase(String searchTerm) {
    return (root, query, cb) -> {
      String containsLikePattern = getContainsLikePattern(searchTerm);
      return cb.like(cb.lower(root.<String>get("name")), containsLikePattern);
    };
  }

  public static Specification<Category> parentIsNull() {
    return (root, query, cb) -> cb.isNull(root.<Category>get("parent"));
  }

  public static Specification<Category> parentIsNotNull() {
    return (root, query, cb) -> cb.isNotNull(root.<Category>get("parent"));
  }

  public static Specification<Category> isNotDeleted() {
    return (root, query, cb) -> cb.notEqual(root.<Category>get("deleted"), true);
  }

  private static String getContainsLikePattern(String searchTerm) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return "%";
    } else {
      return "%" + searchTerm.toLowerCase() + "%";
    }
  }

}
