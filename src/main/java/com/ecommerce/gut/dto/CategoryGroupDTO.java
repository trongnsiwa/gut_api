package com.ecommerce.gut.dto;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.ecommerce.gut.entity.Category;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryGroupDTO {

  private Long id;

  @NotBlank(message = "Name must not be blank.")
  @Size(max = 50, message = "Name must not be higher than 50 characters.")
  private String name;
  
  private Collection<Category> categories = new ArrayList<>();
  
}