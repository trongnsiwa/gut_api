package com.ecommerce.gut.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.ecommerce.gut.dto.ColorSizeDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.CategoryGroup;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.payload.request.ProductRequest;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductColorSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.impl.ProductServiceImpl;
import com.ecommerce.gut.util.CustomResponseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class ProductServiceTest {
  
  @Mock
  private ProductRepository productRepository;

  @Mock
  private ColorRepository colorRepository;

  @Mock
  private PSizeRepository pSizeRepository;

  @Mock
  private ProductImageRepository imageRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CustomResponseEntity customResponseEntity;

  @Mock
  private ProductColorSizeRepository productColorSizeRepository;

  private ProductService productService;

  @BeforeEach
  void setUp() {
    productService = new ProductServiceImpl(productRepository, colorRepository, pSizeRepository, imageRepository, categoryRepository, customResponseEntity, productColorSizeRepository);
  }

  @Test
  void getProductDetailTest_success() throws CustomNotFoundException {
    Product expected = createProduct();

    Mockito.when(productRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

    Product actual = productService.getProductDetail(expected.getId());

    assertEquals(expected, actual);
  }

  @Test
  void getProductDetailTest_throwException() {
    Long id = Long.valueOf(2);

    Mockito.when(productRepository.findById(id)).thenThrow(CustomNotFoundException.class);

    assertThrows(CustomNotFoundException.class, () -> productService.getProductDetail(id));
  }

  @Test
  void addProductToCategoryTest_success() {
    Map<Long, Integer> sizes = new HashMap<>();
    sizes.put(Long.valueOf(1), 10);
    sizes.put(Long.valueOf(2), 0);
    ColorSizeDTO colorSizeDTO = new ColorSizeDTO(Long.valueOf(1), sizes);

    Set<ColorSizeDTO> colorSizeDTOs = new HashSet<>();
    colorSizeDTOs.addAll(Arrays.asList(colorSizeDTO));

    ProductRequest productRequest = new ProductRequest(Long.valueOf(1), "Product 1", Double.valueOf(10), "shortDesc", "longDesc", "material", "handling", true, true, Double.valueOf(5), new Date(), java.sql.Date.valueOf(LocalDate.now().plusDays(5)), colorSizeDTOs);
    Long categoryId = Long.valueOf(1);

    Category category = createCategory();

    Color color = new Color(Long.valueOf(1), "Color", "Source");
    PSize psize = new PSize(Long.valueOf(1), "S"); 
    PSize psize2 = new PSize(Long.valueOf(2), "M"); 

    Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    if (productRequest.getId() > 0) {
      Mockito.when(productRepository.existsById(productRequest.getId())).thenReturn(false);
    }

    Product product = createProduct();
    product.addColorSize(color, psize, 10);
    product.addColorSize(color, psize2, 0);
    
    Mockito.when(colorRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(color));
    Mockito.when(pSizeRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(psize));
    Mockito.when(pSizeRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(psize2));

    ResponseEntity<?> expected = customResponseEntity
    .generateMessageResponseEntity(String.format("Add product %s to category %s successful!",
        product.getName(), category.getName()), HttpStatus.CREATED);

    ResponseEntity<?> actual = productService.addProductToCategory(productRequest, categoryId);

    assertEquals(expected, actual);
  }

 

  public CategoryGroup createCategoryGroup() {
    return new CategoryGroup(Long.valueOf(1), "Group 1");
   }
   
   public Category createCategory() {
     return new Category(Long.valueOf(1), "Category 1");
   }
 
   public Product createProduct() {
     return new Product(Long.valueOf(1), "Product 1", Double.valueOf(10), new Date(), true, true, Double.valueOf(5), new Date(), java.sql.Date.valueOf(LocalDate.now().plusDays(5)), createCategory());
   }

}
