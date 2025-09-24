package com.example.barogo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.barogo.auth.domain.User;
import com.example.barogo.auth.dto.LoginRequestDto;
import com.example.barogo.auth.dto.SignupRequestDto;
import com.example.barogo.auth.repository.UserRepository;
import com.example.barogo.common.error.InvalidPasswordException;
import com.example.barogo.common.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthService authService;

  private SignupRequestDto validSignupRequest;

  @BeforeEach
  void setUp() {
    validSignupRequest = new SignupRequestDto("testId", "Password!12345", "테스트유저");
  }

  @Test
  @DisplayName("회원가입 성공")
  void signup_success() {

    given(userRepository.existsByLoginId(validSignupRequest.getId())).willReturn(false);
    given(passwordEncoder.encode(validSignupRequest.getPassword())).willReturn("encodedPassword");

    authService.signup(validSignupRequest);

    then(userRepository).should().save(any(User.class));
  }

  @Test
  @DisplayName("회원가입 실패 - 이미 존재하는 ID")
  void signup_fail_duplicateId() {
    given(userRepository.existsByLoginId(validSignupRequest.getId())).willReturn(true);

    assertThatThrownBy(() -> authService.signup(validSignupRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 등록된 ID 입니다.");
  }

  @Test
  @DisplayName("회원가입 실패 - 비밀번호 규칙 위반")
  void signup_fail_invalidPassword() {
    SignupRequestDto badRequest = new SignupRequestDto("newId", "short", "짧은비번");

    assertThatThrownBy(() -> authService.signup(badRequest))
        .isInstanceOf(InvalidPasswordException.class);
  }

  @Test
  @DisplayName("로그인 성공")
  void login_success() {
    LoginRequestDto loginRequest = new LoginRequestDto("testId", "Password!12345");
    User mockUser = User.builder()
        .loginId("testId")
        .password("encodedPassword")
        .name("테스트유저")
        .build();

    given(userRepository.findByLoginId(loginRequest.getId())).willReturn(Optional.of(mockUser));
    given(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).willReturn(true);
    given(jwtUtil.createToken(mockUser.getLoginId())).willReturn("mockToken");

    String token = authService.login(loginRequest);

    assertThat(token).isEqualTo("mockToken");
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 ID")
  void login_fail_noUser() {
    LoginRequestDto loginRequest = new LoginRequestDto("unknownId", "Password!12345");
    given(userRepository.findByLoginId(loginRequest.getId())).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("존재하지 않는 ID입니다.");
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void login_fail_wrongPassword() {

    LoginRequestDto loginRequest = new LoginRequestDto("testId", "wrongPassword");
    User mockUser = User.builder()
        .loginId("testId")
        .password("encodedPassword")
        .build();

    given(userRepository.findByLoginId(loginRequest.getId())).willReturn(Optional.of(mockUser));
    given(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).willReturn(false);

    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("비밀번호가 일치하지 않습니다.");
  }
}
