package com.jelee.toyauthjwtjpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jelee.toyauthjwtjpa.repository.UserRepository;
import com.jelee.toyauthjwtjpa.security.JwtAuthenticationFilter;
import com.jelee.toyauthjwtjpa.security.JwtProvider;

@Configuration
public class SecurityConfig {

  @Autowired
  private JwtProvider jwtProvider;
  
  @Autowired
  private UserRepository userRepository;
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // 테스트를 위해 잠시 꺼두기
      // H2 콘솔, auth 이후 url 접근 허락
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/index.html", 
        "/css/**", "/js/**", "/h2-console/**", "/auth/**").permitAll()
        .anyRequest().authenticated()
      )
      .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, userRepository), UsernamePasswordAuthenticationFilter.class)
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      // .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/auth/**")) // H2 콘솔, auth경로 이후의 주소는 CSRF 무시
      .headers(headers -> headers.frameOptions().sameOrigin()) // H2에서 iframe 허용
      .formLogin(Customizer.withDefaults()); // 기본 로그인 폼 사용 -> API 서버에서는 보통 사용 안 한다고해서 나중에 삭제 가능
    
      return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
