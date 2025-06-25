package com.jelee.toyauthjwtjpa.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jelee.toyauthjwtjpa.dto.JoinRequest;
import com.jelee.toyauthjwtjpa.dto.LoginRequest;
import com.jelee.toyauthjwtjpa.entity.Role;
import com.jelee.toyauthjwtjpa.entity.User;
import com.jelee.toyauthjwtjpa.repository.UserRepository;
import com.jelee.toyauthjwtjpa.security.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // 비밀번호 암호화
  private final JwtProvider jwtProvider;

  // 회원가입
  public Long register(JoinRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    }

    User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ROLE_USER)
            .build();
    
    return userRepository.save(user).getId(); // JpaRepository를 상속받았기 때문에 .save() 메서드 직접 구현 안해도 사용 가능
  }

  // 로그인
  public String login(LoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    return jwtProvider.generateToken(user.getUsername(), user.getRole().name());
  }
}
