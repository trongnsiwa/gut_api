package com.ecommerce.gut.specification;

import java.util.Set;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

  private ProductSpecification() {}

  public static Specification<Product> isBrandNew() {
    return (root, query, cb) -> cb.isTrue(root.<Boolean>get("brandNew"));
  }

  public static Specification<Product> isSale() {
    return (root, query, cb) -> cb.isTrue(root.<Boolean>get("sale"));
  }

  public static Specification<Product> isNotSale() {
    return (root, query, cb) -> cb.isFalse(root.<Boolean>get("sale"));
  }

  public static Specification<Product> isNotDeleted() {
    return (root, query, cb) -> cb.isFalse(root.<Boolean>get("deleted"));
  }

  public static Specification<Product> nameContainsIgnoreCase(String searchTerm) {
    return (root, query, cb) -> {
      String containsLikePattern = getContainsLikePattern(searchTerm);
      return cb.like(cb.lower(root.<String>get("name")), containsLikePattern);
    };
  }

  public static Specification<Product> categoryEquals(Category category) {
    return (root, query, cb) -> cb.equal(root.<Category>get("category"), category);
  }

  public static Specification<Product> parentEquals(Category parent) {
    return (root, query, cb) -> {
      Join<Product, Category> proCate = root.join("category");
      return cb.equal(proCate.<Category>get("parent"), parent);
    };
  }

  public static Specification<Product> haveColors(Set<Color> colors) {
    return (root, query, cb) -> {
      Path<Color> color = root.join("colorSizes", JoinType.LEFT).<Color>get("color");
      query.distinct(true);
      return color.in(colors);
    };
  }

  public static Specification<Product> haveSizes(Set<PSize> sizes) {
    return (root, query, cb) -> {
      Path<PSize> size = root.join("colorSizes", JoinType.LEFT).<PSize>get("size");
      query.distinct(true);
      return size.in(sizes);
    };
  }

  public static Specification<Product> betweenPrices(Double fromPrice, Double toPrice) {
    return (root, query, cb) -> cb.between(root.<Double>get("price"), fromPrice, toPrice);
  }

  public static Specification<Product> betweenSalePrices(Double fromPrice, Double toPrice) {
    return (root, query, cb) -> cb.between(root.<Double>get("priceSale"), fromPrice, toPrice);
  }

  public static Specification<Product> greaterThanPrice(Double fromPrice) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<Double>get("price"), fromPrice);
  }

  public static Specification<Product> greaterThanSalePrice(Double fromPrice) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<Double>get("priceSale"), fromPrice);
  }

  private static String getContainsLikePattern(String searchTerm) {
    if (searchTerm == null || searchTerm.isEmpty()) {
      return "%";
    } else {
      return "%" + searchTerm.toLowerCase() + "%";
    }
  }

}
