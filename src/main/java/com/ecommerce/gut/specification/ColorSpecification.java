package com.ecommerce.gut.specification;

import com.ecommerce.gut.entity.Color;
import org.springframework.data.jpa.domain.Specification;

public final class ColorSpecification {
  
  private ColorSpecification() {}

  public static Specification<Color> nameEquals(String name) {
    return (root, query, cb) -> {
      return cb.equal(root.<String>get("name"), name);
    };
  }

  public static Specification<Color> sourceEquals(String source) {
    return (root, query, cb) -> {
      return cb.equal(root.<String>get("source"), source);
    };
  }

  public static Specification<Color> nameContainsIgnoreCase(String searchTerm) {
    return (root, query, cb) -> {
      String containsLikePattern = getContainsLikePattern(searchTerm);
      return cb.like(cb.lower(root.<String>get("name")), containsLikePattern);
    };
  }

  private static String getContainsLikePattern(String searchTerm) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return "%";
    } else {
      return "%" + searchTerm.toLowerCase() + "%";
    }
  }

}
