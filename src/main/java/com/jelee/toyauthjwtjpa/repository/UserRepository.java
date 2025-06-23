package com.jelee.toyauthjwtjpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jelee.toyauthjwtjpa.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByUsername(String username); // 로그인 시 아이디로 사용자 찾을 때 사용
  
  boolean existsByUsername(String username); // 회원가입 시 중복 아이디 체크용
}
