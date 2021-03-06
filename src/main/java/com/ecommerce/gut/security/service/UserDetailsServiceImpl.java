package com.ecommerce.gut.security.service;

import javax.transaction.Transactional;

import com.ecommerce.gut.entity.User;
import com.ecommerce.gut.payload.response.ErrorCode;
import com.ecommerce.gut.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.ERR_USER_NOT_FOUND));

    return UserDetailsImpl.build(user);
  }

}
