package com.ecommerce.gut.service;

import java.util.List;

import com.ecommerce.gut.dto.ProductDTO;
import com.ecommerce.gut.dto.SaleProductDTO;
import com.ecommerce.gut.exception.LoadDataFailException;

public interface HomeService {
  
  List<ProductDTO> getNewProducts(Integer size) throws LoadDataFailException;

  List<SaleProductDTO> getSaleProducts(Integer size) throws LoadDataFailException;

}
