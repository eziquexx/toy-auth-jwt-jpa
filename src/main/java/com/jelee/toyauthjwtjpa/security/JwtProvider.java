package com.jelee.toyauthjwtjpa.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
  
  private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
  private final long EXPIRATION = 1000 * 60 * 60 * 24; // 유효 1일

  // Token 생성
  public String generateToken(String username, String role) {
    Date now = new Date(); // 생성된 시간
    Date expiry = new Date(now.getTime() + EXPIRATION); // 생성된 시간 + 1일
    
    return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key)
            .compact();
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 토큰으로 사용자 얻기
  public String getUsername(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key).build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }
  
}
