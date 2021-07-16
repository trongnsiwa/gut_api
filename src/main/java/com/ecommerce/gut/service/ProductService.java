package com.ecommerce.gut.service;

import java.util.List;
import java.util.Optional;
import com.ecommerce.gut.dto.CreateProductDTO;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.UpdateProductDTO;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.CreateDataFailException;
import com.ecommerce.gut.exception.DataNotFoundException;
import com.ecommerce.gut.exception.DeleteDataFailException;
import com.ecommerce.gut.exception.DuplicateDataException;
import com.ecommerce.gut.exception.LoadDataFailException;
import com.ecommerce.gut.exception.UpdateDataFailException;

public interface ProductService {
  
  List<Product> getProductsByCategoryIdPerPage(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy) throws LoadDataFailException, DataNotFoundException;

  Product getProductDetail(Long id) throws DataNotFoundException;

  boolean addProductToCategory(CreateProductDTO productDTO, Long categoryId) throws CreateDataFailException, DataNotFoundException;

  Product updateProduct(UpdateProductDTO productDTO, Long id, Long categoryId) throws UpdateDataFailException, DataNotFoundException;

  boolean deleteProduct(Long id) throws DeleteDataFailException, DataNotFoundException;

  Optional<Product> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id) throws UpdateDataFailException, DataNotFoundException, DuplicateDataException;
}
