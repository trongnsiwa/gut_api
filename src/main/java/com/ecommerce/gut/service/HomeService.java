package com.ecommerce.gut.service;

import java.util.Collection;
import com.ecommerce.gut.entity.Product;

public interface HomeService {
  
  Collection<Product> getNewProducts(int size);

  Collection<Product> getSaleProducts(int size);

}
