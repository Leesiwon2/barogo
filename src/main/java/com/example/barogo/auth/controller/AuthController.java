package com.example.barogo.auth.controller;

import com.example.barogo.auth.dto.LoginRequestDto;
import com.example.barogo.auth.dto.LoginResponseDto;
import com.example.barogo.auth.dto.SignupRequestDto;
import com.example.barogo.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/barogo")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto request) {
    authService.signup(request);
    return ResponseEntity.ok("회원가입이 완료되었습니다.");
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
    String accessToken = authService.login(request);
    return ResponseEntity.ok(new LoginResponseDto(accessToken));
  }
}
