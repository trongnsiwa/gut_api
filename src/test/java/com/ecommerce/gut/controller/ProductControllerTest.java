package com.ecommerce.gut.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import com.ecommerce.gut.dto.ProductDetailDTO;
import com.ecommerce.gut.entity.Product;
import com.ecommerce.gut.exception.CustomNotFoundException;
import com.ecommerce.gut.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProductService productService;

  private Product product;

  @BeforeEach
  void setUp() {
    this.product = new Product(1L, "p1", 10D, "shortDesc", "longDesc", "material", "handling",
        new Date(), new Date(), true, true, 5D, new Date(),
        java.sql.Date.valueOf(LocalDate.now().plusDays(1)), null, Collections.emptyList(),
        Collections.emptySet(), null);
  }

  @Test
  void getProductDetailTest_success() throws Exception {
    final Long productId = 1L;
    final ProductDetailDTO productDetailDTO =
        new ProductDetailDTO(1L, "p1", 10D, "shortDesc", "longDesc", "material", "handling", true,
            true, 5D, new Date(), java.sql.Date.valueOf(LocalDate.now().plusDays(1)),
            Collections.emptyList(), Collections.emptySet());

    when(productService.getProductDetail(productId)).thenReturn(this.product);

    this.mockMvc.perform(get("/api/product/{id}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is(productDetailDTO.getName())));
  }

  @Test
  void getProductDetailTest_notFound() throws Exception {
    final Long productId = 1L;

    when(productService.getProductDetail(productId)).thenThrow(CustomNotFoundException.class);

    this.mockMvc.perform(get("/api/product/{id}", productId))
        .andExpect(status().isNotFound());
  }

}
