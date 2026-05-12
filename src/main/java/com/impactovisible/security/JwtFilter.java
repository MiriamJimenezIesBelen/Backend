package com.impactovisible.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    String path = request.getRequestURI();

    System.out.println("FILTER CHECK: " + path);

    return path.contains("/api/empresas/login")
      || (path.equals("/api/empresas") && request.getMethod().equals("POST"))
      || path.startsWith("/api/ranking");
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {

      String token = authHeader.substring(7);

      if (jwtService.isTokenValid(token)) {

        String correo = jwtService.extractCorreo(token);
        String rol = jwtService.extractRol(token);

        SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(
            correo,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
          )
        );
      }
    }

    filterChain.doFilter(request, response);
  }
}
