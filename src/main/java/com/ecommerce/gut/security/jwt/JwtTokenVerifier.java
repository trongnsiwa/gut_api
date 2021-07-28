package com.ecommerce.gut.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecommerce.gut.security.service.UserDetailsServiceImpl;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

public class JwtTokenVerifier extends OncePerRequestFilter {

  public static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenVerifier.class);

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);

      if (!Strings.isNullOrEmpty(jwt) && jwtUtils.validateJwtToken(jwt)) {
        String email = jwtUtils.getUsernameFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

    } catch (JwtException ex) {
      LOGGER.error("Cannot set user authentication: {}", ex);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Unable to get JWT Token");
    }
    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    String headerAuth = request.getHeader(jwtUtils.getAuthorizationHeader());

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(jwtUtils.getTokenPrefix())) {
      return headerAuth.replace(jwtUtils.getTokenPrefix(), "");
    }

    return null;
  }

}
