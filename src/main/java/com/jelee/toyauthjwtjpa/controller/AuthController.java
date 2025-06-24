package com.jelee.toyauthjwtjpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jelee.toyauthjwtjpa.dto.JoinRequest;
import com.jelee.toyauthjwtjpa.service.AuthService;

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
}
