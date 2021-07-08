package com.ecommerce.gut.security.jwt;

import java.time.LocalDate;

import java.util.Date;

import javax.crypto.SecretKey;

import com.ecommerce.gut.security.service.UserDetailsImpl;

import com.google.common.net.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${application.jwt.secretKey}")
  private String secretKey;

  @Value("${application.jwt.tokenPrefix}")
  private String tokenPrefix;

  @Value("${application.jwt.tokenExpirationAfterDays}")
  private int tokenExpirationAfterDays;

  public SecretKey generateSecretKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuedAt(new Date())
        .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenExpirationAfterDays)))
        .signWith(generateSecretKey())
        .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(String token) {
    try {
      Jwts.parserBuilder()
      .setSigningKey(secretKey)
      .build()
      .parseClaimsJws(token);

      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String getAuthorizationHeader() {
    return HttpHeaders.AUTHORIZATION;
  }

  public String getSecretKey() {
    return this.secretKey;
  }

  public String getTokenPrefix() {
    return this.tokenPrefix;
  }

  public int getTokenExpirationAfterDays() {
    return this.tokenExpirationAfterDays;
  }

}
