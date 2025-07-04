package com.jelee.toyauthjwtjpa.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jelee.toyauthjwtjpa.dto.JoinRequest;
import com.jelee.toyauthjwtjpa.entity.User;
import com.jelee.toyauthjwtjpa.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class AuthServiceTest {
  
  @Autowired
  private UserRepository userRepository;

  private AuthService authService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    authService = new AuthService(userRepository, passwordEncoder, null);
    // authService = new AuthService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName("회원가입 성공 테스트")
  void registerTest() {
    // given
    JoinRequest request = new JoinRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");

    // when
    Long userId = authService.register(request);

    // then
    Optional<User> savedUser = userRepository.findById(userId);
    assertThat(savedUser).isPresent();
    assertThat(savedUser.get().getUsername()).isEqualTo("testuser");
    
    // 비밀번호 매칭 검증
    assertThat(passwordEncoder.matches("testpass", savedUser.get().getPassword())).isTrue();
    System.out.println("저장된 username: " + savedUser.get().getUsername());
    System.out.println("저장된 password: " + savedUser.get().getPassword());
  }

  @Test
  @DisplayName("중복 회원가입 예외 테스트")
  void duplicateRegisterTest() {
    JoinRequest request = new JoinRequest();
    request.setUsername("testuser");
    request.setPassword("1234");

    authService.register(request);
    System.out.println("첫 번째 가입 완료");

    try {
      authService.register(request); // 두 번째 가입 시도 -> 예외 발생
    } catch (IllegalArgumentException e) {
      System.out.println("예외 메시지: " + e.getMessage());
      assertThat(e.getMessage()).contains("이미 사용 중인 아이디");
    }
  }

  // SecurityConfig에서 PasswordEncoder를 @Bean 주입 하였기 때문에 필요 없음.
  // @TestConfiguration
  // static class TestConfig {
  //   @Bean
  //   public PasswordEncoder passwordEncoder() {
  //     return new BCryptPasswordEncoder();
  //   }
  // }
}
