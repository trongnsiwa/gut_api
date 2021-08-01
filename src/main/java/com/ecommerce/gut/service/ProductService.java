package com.ecommerce.gut.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.ecommerce.gut.dto.AddReviewDTO;
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

  List<Product> getProductsPerPage(Integer pageNumber, Integer pageSize, String sortBy, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);

  List<Product> searchProductsByName(Integer pageNumber, Integer pageSize, String sortBy, String name, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);

  Long countProducts(Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);

  Long countProductsByName(String name, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);
  
  List<Product> getProductsByCategoryPerPage(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice) throws LoadDataFailException, DataNotFoundException;

  List<Product> searchProductsByCategoryAndName(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String name, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice) throws LoadDataFailException, DataNotFoundException;

  Long countProductsByCategory(Long categoryId, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);

  Long countProductsByCategoryAndName(Long categoryId, String name, Set<String> saleTypes, Set<Long> colorIds, Set<Long> sizeIds, Double fromPrice, Double toPrice);

  Product getProductDetail(Long id) throws DataNotFoundException;

  boolean addProductToCategory(CreateProductDTO productDTO, Long categoryId) throws CreateDataFailException, DataNotFoundException;

  Product updateProduct(UpdateProductDTO productDTO, Long id, Long categoryId) throws UpdateDataFailException, DataNotFoundException;

  boolean deleteProduct(Long id) throws DeleteDataFailException, DataNotFoundException;

  Optional<Product> replaceImagesOfProduct(ImageListDTO imageListRequest, Long id) throws UpdateDataFailException, DataNotFoundException, DuplicateDataException;

  Product addUserReviewOfProduct(AddReviewDTO reviewRequest) throws UpdateDataFailException, DataNotFoundException;

}
