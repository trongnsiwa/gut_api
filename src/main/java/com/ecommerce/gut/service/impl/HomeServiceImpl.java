package com.ecommerce.gut.service.impl;

import java.util.Collection;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.HomeService;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CustomResponseEntity customResponseEntity;

  @Override
  public Collection<Product> getNewProducts(int size) {
    return productRepository.getNewProducts(size);
  }

  @Override
  public Collection<Product> getSaleProducts(int size) {
    return productRepository.getLimitedProducts(size);
  }
  
}
