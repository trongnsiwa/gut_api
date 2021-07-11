package com.ecommerce.gut.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserDTO {

  private UUID id;
  private String email;
  private String fullName;
  private boolean enabled;
  private Set<String> roles = new HashSet<>();
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date modifiedDate;

}
