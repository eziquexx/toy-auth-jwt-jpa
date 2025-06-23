package com.jelee.toyauthjwtjpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      // H2 콘솔 접근 허용
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/h2-console/**").permitAll()
        .anyRequest().authenticated()
      )
      .headers(headers -> headers.frameOptions().sameOrigin()) // H2에서 iframe 허용
      .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // H2 콘솔은 CSRF 무시
      .formLogin(Customizer.withDefaults()); // 기본 로그인 폼 사용
    
      return http.build();
  }
}
