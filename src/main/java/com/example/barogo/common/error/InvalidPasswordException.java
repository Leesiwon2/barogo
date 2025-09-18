package com.example.barogo.common.error;

public class InvalidPasswordException extends RuntimeException{
  public InvalidPasswordException(String message) {
    super(message);
  }
}
