package com.example.barogo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {
  @NotBlank
  private String id;

  @NotBlank
  private String password;
}
