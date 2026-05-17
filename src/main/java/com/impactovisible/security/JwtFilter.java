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

    String method = request.getMethod();

    System.out.println("FILTER CHECK: " + path);

    // ✅ AÑADE ESTO — dejar pasar OPTIONS (preflight CORS)
    if (method.equals("OPTIONS")) return true;

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
    System.out.println("=== JWT FILTER ===");
    System.out.println("URL: " + request.getRequestURI());
    System.out.println("Method: " + request.getMethod());
    System.out.println("Auth header: " + authHeader); // ← ¿llega el token?

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      boolean valid = jwtService.isTokenValid(token);
      System.out.println("Token válido: " + valid); // ← ¿es válido?

      if (valid) {
        String correo = jwtService.extractCorreo(token);
        String rol = jwtService.extractRol(token);
        System.out.println("Correo: " + correo + " | Rol: " + rol); // ← ¿extrae bien?

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
