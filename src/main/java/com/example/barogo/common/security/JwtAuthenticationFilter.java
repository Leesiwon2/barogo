package com.example.barogo.common.security;

import com.example.barogo.auth.domain.User;
import com.example.barogo.auth.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();

    // Swagger 관련 요청은 필터 타지 않도록 예외 처리
    if (path.startsWith("/v3/api-docs") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/swagger-resources") ||
        path.startsWith("/webjars") ||
        path.equals("/swagger-ui.html")) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7); // Bearer 이후

    try {
      if (jwtUtil.validateToken(token)) {
        String email = jwtUtil.getIdFromToken(token);
        User user = userRepository.findById(email).orElse(null);

        if (user != null) {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  user, null, user.getAuthorities());

          authentication.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );

          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (JwtException e) {
      // 유효하지 않은 토큰일 경우 인증 없이 진행
    }

    filterChain.doFilter(request, response);
  }

}
