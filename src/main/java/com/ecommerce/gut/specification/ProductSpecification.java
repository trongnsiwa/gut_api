package com.ecommerce.gut.specification;

import javax.persistence.criteria.Join;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

  private ProductSpecification() {}

  public static Specification<Product> isBrandNew() {
    return (root, query, cb) -> cb.equal(root.<Boolean>get("brandNew"), true);
  }

  public static Specification<Product> isSale() {
    return (root, query, cb) -> cb.equal(root.<Boolean>get("sale"), true);
  }

  public static Specification<Product> isNotSale() {
    return (root, query, cb) -> cb.notEqual(root.<Boolean>get("sale"), true);
  }

  public static Specification<Product> isNotDeleted() {
    return (root, query, cb) -> cb.notEqual(root.<Boolean>get("deleted"), true);
  }

  public static Specification<Product> nameContainsIgnoreCase(String searchTerm) {
    return (root, query, cb) -> {
      String containsLikePattern = getContainsLikePattern(searchTerm);
      return cb.like(cb.lower(root.<String>get("name")), containsLikePattern);
    };
  }

  public static Specification<Product> categoryEquals(Category category) {
    return (root, query, cb) -> {
      return cb.equal(root.<Category>get("category"), category);
    };
  }

  public static Specification<Product> parentEquals(Category parent) {
    return (root, query, cb) -> {
      Join<Product, Category> proCate = root.join("category");
      return cb.equal(proCate.<Category>get("parent"), parent);
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
