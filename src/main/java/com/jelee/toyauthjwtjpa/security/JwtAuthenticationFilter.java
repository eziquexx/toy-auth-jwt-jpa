package com.jelee.toyauthjwtjpa.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jelee.toyauthjwtjpa.entity.User;
import com.jelee.toyauthjwtjpa.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
      throws ServletException, IOException {

        // 1. 쿠키에서 JWT 찾기
        String token = null;
        if (request.getCookies() != null) {
          token = Arrays.stream(request.getCookies())
                  .filter(c -> c.getName().equals("JWT"))
                  .findFirst()
                  .map(Cookie::getValue)
                  .orElse(null);
        }

        // 2. JWT 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
          String username = jwtProvider.getUsername(token);

          // 3. 사용자 정보 조회
          User user = userRepository.findByUsername(username).orElse(null);
          if (user != null) {
            // 4. 인증 객체 생성 및 등록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        }

        // 5. 다음 필터로 이동
        filterChain.doFilter(request, response);
      }
}
