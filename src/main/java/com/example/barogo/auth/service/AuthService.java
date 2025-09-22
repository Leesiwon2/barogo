package com.example.barogo.auth.service;

import com.example.barogo.auth.domain.User;
import com.example.barogo.auth.dto.LoginRequestDto;
import com.example.barogo.auth.dto.SignupRequestDto;
import com.example.barogo.auth.repository.UserRepository;
import com.example.barogo.common.error.InvalidPasswordException;
import com.example.barogo.common.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public void signup(SignupRequestDto request) {
    if (userRepository.existsById(request.getId())) {
      throw new IllegalArgumentException("이미 등록된 ID 입니다.");
    }

    if (!isValidPassword(request.getPassword())) {
      throw new InvalidPasswordException("비밀번호는 12자 이상이며, 대문자, 소문자, 숫자, 특수문자 중 3가지 이상을 포함해야 합니다.");
    }

    User user = User.builder()
        .id(request.getId())
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .build();

    userRepository.save(user);
  }

  public String login(LoginRequestDto request) {

    User user = userRepository.findById(request.getId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID입니다."));


    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
    return jwtUtil.createToken(user.getId());
  }

  private boolean isValidPassword(String password) {
    if (password.length() < 12) return false;

    int count = 0;
    if (password.matches(".*[A-Z].*")) count++;
    if (password.matches(".*[a-z].*")) count++;
    if (password.matches(".*[0-9].*")) count++;
    if (password.matches(".*[!@#$%^&*()\\-_=+\\[{\\]}|;:'\",<.>/?`~].*")) count++;

    return count >= 3;
  }
}
