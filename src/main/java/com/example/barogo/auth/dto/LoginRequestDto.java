package com.example.barogo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequestDto {
  @NotBlank
  private String id;

  @NotBlank
  private String password;
}
