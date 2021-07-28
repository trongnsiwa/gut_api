package com.ecommerce.gut.dto;

import javax.validation.constraints.NotBlank;

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
public class UserAvatarDTO {
  
  private Long id;

  @NotBlank(message = "{image.url.notBlank}")
  private String imageUrl;

  private String title;

}
