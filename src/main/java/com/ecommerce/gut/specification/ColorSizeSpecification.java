package com.ecommerce.gut.specification;

import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ColorSizeSpecification {

  private ColorSizeSpecification() {}

  public static Specification<ColorSize> colorEquals(Color color) {
    return (root, query, cb) -> cb.equal(root.<Color>get("color"), color);
  }

  public static Specification<ColorSize> sizeEquals(PSize size) {
    return (root, query, cb) -> cb.equal(root.<PSize>get("size"), size);
  }

  public static Specification<ColorSize> productEquals(Product product) {
    return (root, query, cb) -> cb.equal(root.<Product>get("product"), product);
  }

}
