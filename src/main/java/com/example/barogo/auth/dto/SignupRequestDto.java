package com.example.barogo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {
  @NotBlank
  private String id;

  @NotBlank
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+-=\\[\\]{};':\"|,.<>\\/?]).{12,}$",
      message = "비밀번호는 영어 대소문자, 숫자, 특수문자를 포함한 12자리 이상이어야 합니다.")
  private String password;

  @NotBlank
  private String name;
}
