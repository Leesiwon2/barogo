package com.example.barogo.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.barogo.auth.dto.LoginRequestDto;
import com.example.barogo.auth.dto.SignupRequestDto;
import com.example.barogo.auth.repository.UserRepository;
import com.example.barogo.auth.service.AuthService;
import com.example.barogo.common.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerRestDocsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AuthService authService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private JwtUtil jwtUtil;


  @Test
  @DisplayName("회원가입 API 문서화")
  void signup() throws Exception {
    SignupRequestDto request = new SignupRequestDto("testuser", "TestPassword123!", "홍길동");

    mockMvc.perform(post("/barogo/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
        .andDo(document("signup",
            requestFields(
                fieldWithPath("id").description("아이디"),
                fieldWithPath("password").description("비밀번호"),
                fieldWithPath("name").description("이름")
            ),
            responseFields(
                fieldWithPath("message").description("응답 메시지")
            )
        ));
  }

  @Test
  @DisplayName("로그인 API 문서화")
  void login() throws Exception {
    LoginRequestDto request = new LoginRequestDto("testuser", "TestPassword123!");
    Mockito.when(authService.login(any(LoginRequestDto.class))).thenReturn("mocked-jwt-token");

    mockMvc.perform(post("/barogo/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"))
        .andDo(document("login",
            requestFields(
                fieldWithPath("id").description("아이디"),
                fieldWithPath("password").description("비밀번호")
            ),
            responseFields(
                fieldWithPath("accessToken").description("JWT 액세스 토큰")
            )
        ));
  }
}
