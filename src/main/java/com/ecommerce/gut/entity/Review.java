package com.ecommerce.gut.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_reviews")
public class Review {
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String title;

  @Column(name = "comment", columnDefinition = "TEXT")
  private String comment;

  private Integer rating;

  @CreationTimestamp
  private LocalDateTime datetime;

  public Review(Product product, User user, String title, String comment, Integer rating) {
    this.product = product;
    this.user = user;
    this.title = title;
    this.comment = comment;
    this.rating = rating;
  }
  
}
