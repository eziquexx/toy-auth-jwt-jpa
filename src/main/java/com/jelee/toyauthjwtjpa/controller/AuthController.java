package com.jelee.toyauthjwtjpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jelee.toyauthjwtjpa.dto.JoinRequest;
import com.jelee.toyauthjwtjpa.dto.LoginRequest;
import com.jelee.toyauthjwtjpa.entity.User;
import com.jelee.toyauthjwtjpa.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  
  private final AuthService authService;

  // 회원가입 API
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody JoinRequest request) {
    Long userId = authService.register(request);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("회원가입이 완료되었습니다. userId: " + userId);
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    String token = authService.login(request);

    // JWT를 HttpOnly 쿠키에 저장
    ResponseCookie cookie = ResponseCookie.from("JWT", token)
            .httpOnly(true)
            .secure(false) // HTTPS 환경에서 true로 설절ㅇ
            .path("/")
            .maxAge(24 * 60 * 60)
            .sameSite("Strict")
            .build();
    
    response.addHeader("Set-Cookie", cookie.toString());

    return ResponseEntity.ok("로그인 성공");
  }

  @GetMapping("/me")
  public ResponseEntity<?> getMyInfo(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok("로그인한 사용자: " + user.getUsername());
  }
}
