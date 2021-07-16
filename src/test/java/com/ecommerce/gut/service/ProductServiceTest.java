package com.ecommerce.gut.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.ecommerce.gut.dto.ColorSizeDTO;
import com.ecommerce.gut.dto.CreateProductDTO;
import com.ecommerce.gut.dto.ImageListDTO;
import com.ecommerce.gut.dto.ProductImageDTO;
import com.ecommerce.gut.dto.UpdateProductDTO;
import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.repository.CategoryRepository;
import com.ecommerce.gut.repository.ColorRepository;
import com.ecommerce.gut.repository.ColorSizeRepository;
import com.ecommerce.gut.repository.ImageRepository;
import com.ecommerce.gut.repository.PSizeRepository;
import com.ecommerce.gut.repository.ProductImageRepository;
import com.ecommerce.gut.repository.ProductRepository;
import com.ecommerce.gut.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceTest {
  
  @Mock
  private ProductRepository productRepository;

  @Mock
  private ColorRepository colorRepository;

  @Mock
  private PSizeRepository sizeRepository;

  @Mock
  private ProductImageRepository productImageRepository;

  @Mock
  private ImageRepository imageRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ColorSizeRepository colorSizeRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private ProductServiceImpl productService;

  @BeforeEach
  public void setUp() throws Exception {

    Category parent = new Category(2L, "Cate Parent");

    Category category = new Category();
    category.setId(1L);
    category.setName("Cate 1");
    category.setParent(parent);
    parent.getSubCategories().add(category);

    Mockito.when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
    Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    Product product = new Product();
    product.setId(1L);
    product.setName("Product 1");
    product.setPrice(700D);
    product.setCategory(category);
    
    Product product1 = new Product();
    product1.setId(2L);
    product1.setName("Product 2");
    product1.setPrice(900D);
    product1.setCategory(category);

    Color color = new Color(1L, "Color 1", "Source 1");
    Color color1 = new Color(2L, "Color 2", "Source 2");
    Mockito.when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
    Mockito.when(colorRepository.findById(2L)).thenReturn(Optional.of(color1));

    Image image = new Image("imageUrl", "title");
    image.setId(1L);
    Image image2 = new Image("imageUrl2", "title2");
    image2.setId(2L);
    Mockito.when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
    Mockito.when(imageRepository.findById(2L)).thenReturn(Optional.of(image2));

    product.addImage(image, 1L);
    product.addImage(image2, 2L);

    Mockito.when(productImageRepository.findImagesByProduct(product)).thenReturn(product.getProductImages());

    PSize size = new PSize(1L, "Size 1");
    Mockito.when(sizeRepository.findById(1L)).thenReturn(Optional.of(size));

    PSize size2 = new PSize(2L, "Size 2");
    Mockito.when(sizeRepository.findById(2L)).thenReturn(Optional.of(size2));
    
    product.addColorSize(color, size, 10);
    product.addColorSize(color, size2, 9);
    product.addColorSize(color1, size2, 2);

    Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    Mockito.when(productRepository.findById(2L)).thenReturn(Optional.of(product1));

  }

  @Test
  public void testGetProductsByCategoryIdPerPage() throws Exception {
    CreateProductDTO productDTO = new CreateProductDTO();
    productDTO.setName("name");
    productDTO.setPrice(500D);
    
    ColorSizeDTO colorSizeDTO = new ColorSizeDTO();
    Map<Long, Integer> sizes = new HashMap<>();
    sizes.put(1L, 9);
    sizes.put(2L, 10);
    colorSizeDTO.setColorId(1L);
    colorSizeDTO.setSizes(sizes);
    
    ColorSizeDTO colorSizeDTO2 = new ColorSizeDTO();
    Map<Long, Integer> sizes2 = new HashMap<>();
    sizes2.put(1L, 9);
    colorSizeDTO2.setColorId(2L);
    colorSizeDTO2.setSizes(sizes2);

    Set<ColorSizeDTO> colorSizeDTOs = new HashSet<>();
    colorSizeDTOs.add(colorSizeDTO);
    colorSizeDTOs.add(colorSizeDTO2);

    productDTO.setColors(colorSizeDTOs);

    Category category = categoryRepository.findById(1L).get();
    Product expected = new Product();
    expected.setName("name");
    expected.setPrice(500D);
    expected.setBrandNew(true);
    expected.setCategory(category);
    expected.setDeleted(false);
    expected.setInStock(true);

    Mockito.when(modelMapper.map(any(), any())).thenReturn(expected);

    Color color1 = colorRepository.findById(1L).get();
    Color color2 = colorRepository.findById(2L).get();
    PSize size1 = sizeRepository.findById(1L).get();
    PSize size2 = sizeRepository.findById(2L).get();

    expected.addColorSize(color1, size1, 9);
    expected.addColorSize(color1, size2, 10);
    expected.addColorSize(color2, size1, 9);
    expected.addColorSize(color2, size2, 5);

    Mockito.when(productRepository.save(expected)).thenAnswer(invocation -> invocation.getArgument(0));

    assertEquals(true, productService.addProductToCategory(productDTO, 1L));

    Mockito.verify(productRepository).save(any(Product.class));
    
  }

  @Test
  public void testUpdateProductSuccess() throws Exception {
    UpdateProductDTO productDTO = new UpdateProductDTO();
    productDTO.setId(1L);
    productDTO.setName("name");
    productDTO.setPrice(500D);
    
    ColorSizeDTO colorSizeDTO = new ColorSizeDTO();
    Map<Long, Integer> sizes = new HashMap<>();
    sizes.put(1L, 9);
    colorSizeDTO.setColorId(1L);
    colorSizeDTO.setSizes(sizes);
    
    ColorSizeDTO colorSizeDTO2 = new ColorSizeDTO();
    Map<Long, Integer> sizes2 = new HashMap<>();
    sizes2.put(1L, 9);
    colorSizeDTO2.setColorId(2L);
    colorSizeDTO2.setSizes(sizes2);

    Set<ColorSizeDTO> colorSizeDTOs = new HashSet<>();
    colorSizeDTOs.add(colorSizeDTO);
    colorSizeDTOs.add(colorSizeDTO2);

    productDTO.setColors(colorSizeDTOs);

    Category category = categoryRepository.findById(1L).get();
    Product expected = new Product();
    expected.setId(1L);
    expected.setName("name");
    expected.setPrice(500D);
    expected.setBrandNew(false);
    expected.setCategory(category);
    expected.setDeleted(false);
    expected.setInStock(true);

    Color color1 = colorRepository.findById(1L).get();
    Color color2 = colorRepository.findById(2L).get();
    PSize size1 = sizeRepository.findById(1L).get();

    expected.addColorSize(color1, size1, 9);
    expected.addColorSize(color2, size1, 9);

    Mockito.when(productRepository.save(expected)).thenReturn(expected);

    Product product = productService.updateProduct(productDTO, 1L, 1L);
    assertNotNull(product);
    assertEquals(3, product.getColorSizes().size());
    
    Mockito.verify(productRepository).save(any(Product.class));
  }

  @Test
  public void testReplaceEmptyImagesSuccess() throws Exception {
    ImageListDTO dto = new ImageListDTO();
    List<ProductImageDTO> images = new ArrayList<>();
    dto.setImages(images);

    Product product = productRepository.findById(1L).get();
    product.getProductImages().clear();

    Mockito.when(productRepository.save(product)).thenReturn(product);

    Optional<Product> products = productService.replaceImagesOfProduct(dto, 1L);
    assertEquals(true, products.get().getProductImages().isEmpty());
  }

  @Test
  public void testReplaceExistedImagesSuccess() throws Exception {
    ImageListDTO dto = new ImageListDTO();
    List<ProductImageDTO> images = new ArrayList<>();
    ProductImageDTO img3 = new ProductImageDTO("imageUrl3", "title3", 1L);
    images.add(img3);
    dto.setImages(images);

    Mockito.when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Optional<Product> products = productService.replaceImagesOfProduct(dto, 1L);
    assertEquals(1, products.get().getProductImages().size());

    // assertThrows(DuplicateDataException.class, () -> productService.replaceImagesOfProduct(dto, 1L));
    
  }

}
