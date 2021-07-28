package com.ecommerce.gut.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import com.ecommerce.gut.entity.Category;
import com.ecommerce.gut.entity.Color;
import com.ecommerce.gut.entity.ColorSize;
import com.ecommerce.gut.entity.Image;
import com.ecommerce.gut.entity.PSize;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.entity.ProductImage;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductRepositoryTest {
  
  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ColorRepository colorRepository;

  @Autowired
  private PSizeRepository sizeRepository;

  @Autowired
  private ImageRepository imageRepository;
  
  @Autowired
  private ProductImageRepository productImageRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ColorSizeRepository colorSizeRepository;

  @Test
  public void testSaveProductSuccess() throws Exception {
    Product product = new Product();
    product.setName("Product 1");
    product.setPrice(1000D);

    Category category = categoryRepository.findById(161L).get();
    product.setCategory(category);

    addColorSizeToProduct(1L, 1L, 1, product);
    addColorSizeToProduct(1L, 2L, 0, product);
    addColorSizeToProduct(1L, 3L, 2, product);
    addColorSizeToProduct(2L, 1L, 1, product);
    addColorSizeToProduct(2L, 2L, 0, product);
    addColorSizeToProduct(2L, 3L, 1, product);
    
    Product saveProduct = productRepository.save(product);

    assertNotNull(saveProduct);
    assertEquals(161L, saveProduct.getCategory().getId());
    assertEquals(6, saveProduct.getColorSizes().size());
  }

  @Test
  public void testUpdateProductSuccess() throws Exception {
    Product product = productRepository.findById(164L).get();
    Category category = categoryRepository.findById(162L).get();
    product.setCategory(category);

    Color color = colorRepository.findById(1L).get();
    PSize size = sizeRepository.findById(1L).get();

    ColorSize removedColorSize1 = colorSizeRepository.getColorSizeOfProduct(product, color, size).get();

    Set<ColorSize> colorSizes = colorSizeRepository.findColorSizesByProductId(164L);

    product.getColorSizes().clear();
    colorSizes.remove(removedColorSize1);
    product.getColorSizes().addAll(colorSizes);

    Product saveProduct = productRepository.save(product);

    assertNotNull(saveProduct);
    assertEquals(162L, saveProduct.getCategory().getId());
    assertEquals(5, saveProduct.getColorSizes().size());

  }

  @Test
  public void testReplaceImagesOfProductSuccess() throws Exception {
    Product product = productRepository.findById(164L).get();
    Image img1 = new Image("url 1", "title 1");
    Image img2 = new Image("url 2", "title 2");
    Image img3 = new Image("url 3", "title 3");

    imageRepository.save(img1);
    imageRepository.save(img2);
    imageRepository.save(img3);

    product.addImage(img1, 1L);
    product.addImage(img2, 2L);
    product.addImage(img3, 0L);

    Product saveProduct = productRepository.save(product);

    assertNotNull(saveProduct);
    assertEquals(3, saveProduct.getProductImages().size());
  }

  @Test
  public void testUpdateColorCodeImageSuccess() throws Exception {
    Product product = productRepository.findById(164L).get();

    Image image = imageRepository.findById(214L).get();

    product.getProductImages().stream()
              .forEach(productImg -> {
                if (productImg.getImage().equals(image)) {
                  productImg.setColorCode(2L);
                }
              });
    
    assertNotNull(productRepository.save(product));
  }

  @Test
  public void testClearALLImagesOfProductSuccess() {
    Product product = productRepository.findById(164L).get();

    Set<ProductImage> productImages = productImageRepository.findImagesByProduct(product);

    product.getProductImages().clear();

    Product saveProduct = productRepository.save(product);

    productImages.stream().forEach((image) -> {
      imageRepository.deleteById(image.getImage().getId());
    });

    assertNotNull(saveProduct);
    assertEquals(true, productImageRepository.findImagesByProduct(product).isEmpty());
  }


  private void addColorSizeToProduct(Long colorId, Long sizeId, int quantity, Product product) {
    Color color = colorRepository.findById(colorId).get();
    PSize size = sizeRepository.findById(sizeId).get();
    product.addColorSize(color, size, quantity);
  }

}
