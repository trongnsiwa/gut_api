package com.ecommerce.gut.dto;

import java.time.LocalDateTime;
import java.util.UUID;
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
public class ReviewDTO {
  
  private Long id;
  private Long productId;
  private UUID userId;
  private String userName;
  private String title;
  private String comment;
  private Integer rating;
  private LocalDateTime datetime; 

}
