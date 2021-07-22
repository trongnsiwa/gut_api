package com.ecommerce.gut.security;

import com.ecommerce.gut.security.jwt.JwtAuthEntryPoint;
import com.ecommerce.gut.security.jwt.JwtTokenVerifier;
import com.ecommerce.gut.security.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  JwtAuthEntryPoint jwtAuthEntryPoint;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtTokenVerifier authenticationJwtTokenFilter() {
    return new JwtTokenVerifier();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .exceptionHandling()
        .authenticationEntryPoint(jwtAuthEntryPoint)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors().and()
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/auth/**").permitAll()
        .antMatchers("/home/**").permitAll()
        .antMatchers("/category/**").permitAll()
        .antMatchers("/color/**").permitAll()
        .antMatchers("/product/**").permitAll()
        .antMatchers("/cart/**").permitAll()
        .anyRequest().authenticated();

    http.addFilterBefore(authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
        "/swagger-resources/**", "/swagger-resources");
  }
}
