package com.ecommerce.gut.dto;

import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewDTO {

  @NotNull(message = "{review.productId.notNull}")
  private Long productId;
  
  @NotNull(message = "{review.userId.notNull}")
  private UUID userId;

  @NotBlank(message = "{review.title.notBlank}")
  private String title;
  
  @NotBlank(message = "{review.comment.notBlank}")
  private String comment;
  
  @NotNull(message = "{review.rating.notNull}")
  @Min(value = 1, message = "{review.rating.min}")
  private Integer rating;

}
