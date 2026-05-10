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

    String path = request.getServletPath();
    String method = request.getMethod();

    System.out.println(">>> SHOULD NOT FILTER: " + method + " " + path);

    // LOGIN
    if (
      path.contains("/api/empresas/login")
    ) {
      return true;
    }

    // REGISTRO
    if (
      path.equals("/api/empresas")
        && method.equals("POST")
    ) {
      return true;
    }

    // PING
    if (
      path.equals("/api/empresas")
        && method.equals("GET")
    ) {
      return true;
    }

    // RANKING
    if (
      path.startsWith("/api/ranking")
    ) {
      return true;
    }

    return false;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader =
      request.getHeader("Authorization");

    System.out.println(">>> AUTH HEADER: " + authHeader);

    // NO TOKEN
    if (
      authHeader == null ||
        !authHeader.startsWith("Bearer ")
    ) {

      filterChain.doFilter(request, response);

      return;
    }

    try {

      String token = authHeader.substring(7);

      boolean valid =
        jwtService.isTokenValid(token);

      System.out.println(">>> TOKEN VALIDO: " + valid);

      if (valid) {

        String correo =
          jwtService.extractCorreo(token);

        String rol =
          jwtService.extractRol(token);

        UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(
            correo,
            null,
            List.of(
              new SimpleGrantedAuthority(
                "ROLE_" + rol
              )
            )
          );

        SecurityContextHolder
          .getContext()
          .setAuthentication(auth);
      }

    } catch (Exception e) {

      e.printStackTrace();
    }

    filterChain.doFilter(request, response);
  }
}
