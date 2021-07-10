package com.ecommerce.gut.util;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.entity.Product;
import org.apache.commons.lang3.time.DateUtils;

public class EntityTest {

  public static CategoryGroup categoryGroup1() {
   return new CategoryGroup(Long.valueOf(1), "Group 1");
  }
  
  public static Category category1() {
    return new Category(Long.valueOf(1), "Category 1");
  }

  public static Date parseToDate(String sdate) throws ParseException {
    return DateUtils.parseDate(sdate, "yyyy-MM-dd HH:mm:ss");
  }

  public static Product product1() throws ParseException {
    // Product with new, sale, highest price and highest sale price
    return new Product(Long.valueOf(1), "Product 1", Double.valueOf(10), parseToDate("2021-07-10 01:37:18"), true, true, Double.valueOf(5), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-15 01:37:18"), category1());
  }

  public static Product product2() throws ParseException {
    // Product with not new, sale, highest price and highest sale price
    return new Product(Long.valueOf(2), "Product 2", Double.valueOf(10), parseToDate("2021-07-10 01:37:18"), false, true, Double.valueOf(5), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product3() throws ParseException {
    // Product with not new, not sale, highest price
    return new Product(Long.valueOf(3), "Product 3", Double.valueOf(10), parseToDate("2021-07-10 01:37:18"), false, false, category1());
  }

  public static Product product4() throws ParseException {
    // Product with not new, not sale, lowest price
    return new Product(Long.valueOf(4), "Product 4", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), false, false, category1());
  }

  public static Product product5() throws ParseException {
    // Product with new, not sale, lowest price
    return new Product(Long.valueOf(5), "Product 5", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), true, false, category1());
  }

  public static Product product6() throws ParseException {
    // Product with new, sale, lowest price and lowest sale price
    return new Product(Long.valueOf(6), "Product 6", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), true, true, Double.valueOf(1), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product7() throws ParseException {
    // Product with not new, sale, highest price and lowest sale price
    return new Product(Long.valueOf(7), "Product 7", Double.valueOf(10), parseToDate("2021-07-10 01:37:18"), false, true, Double.valueOf(1), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product8() throws ParseException {
    // Product with not new, sale, lowest price and highest sale price
    return new Product(Long.valueOf(8), "Product 8", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), false, true, Double.valueOf(5), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product9() throws ParseException {
    // Product with not new, sale, lowest price and lowest sale price
    return new Product(Long.valueOf(9), "Product 9", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), false, true, Double.valueOf(1), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product10() throws ParseException {
    // Product with new, sale, highest price and lowest sale price
    return new Product(Long.valueOf(10), "Product 10", Double.valueOf(10), parseToDate("2021-07-10 01:37:18"), true, true, Double.valueOf(1), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product11() throws ParseException {
    // Product with new, sale, lowest price and highest sale price
    return new Product(Long.valueOf(11), "Product 11", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), true, true, Double.valueOf(5), parseToDate("2021-07-10 01:37:18"), parseToDate("2021-07-14 01:37:18"), category1());
  }

  public static Product product12() throws ParseException {
    // Product with new, not sale, highest price
    return new Product(Long.valueOf(11), "Product 11", Double.valueOf(6), parseToDate("2021-07-10 01:37:18"), true, false, category1());
  }

  public static List<Product> cheapestPriceproductList() throws ParseException {
    /*
      price -> asc
      sale -> desc (true first)
      priceSale -> asc
        saleFromDate -> asc
            saleToDate -> asc
      brandNew -> desc
      updatedDate -> desc 
    */
    return Arrays.asList(product6(), product9(), product10(), product7(), product11(), product8(), product1(), product2(), product5(), product4(), product3());
  }

  public static List<Product> highestPriceProductList() throws ParseException {
    return Arrays.asList(product12(), product1(), product10(), product11(), product6(), product5(), product2(), product7(), product3(), product8(), product9(), product4());
  }

  public static List<Product> defaultProductList() throws ParseException {
    return Arrays.asList(product1(), product6(), product10(), product11(), product12(), product5(), product2(), product7(), product8(), product9(), product3(), product4());
  }

}
