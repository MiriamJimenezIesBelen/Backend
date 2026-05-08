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
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
    throws ServletException, IOException {

    String path = request.getServletPath();
    String method = request.getMethod();

    System.out.println(">>> REQUEST: " + method + " " + path);

    if (
      path.startsWith("/api/empresas/login") ||
        (path.equals("/api/empresas") && method.equals("POST")) ||
        path.startsWith("/api/ranking")
    ) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    System.out.println(">>> AUTH HEADER: " + authHeader);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      System.out.println(">>> TOKEN: " + token);

      boolean valid = jwtService.isTokenValid(token);
      System.out.println(">>> TOKEN VÁLIDO: " + valid);

      if (valid) {
        String correo = jwtService.extractCorreo(token);
        String rol = jwtService.extractRol(token);
        System.out.println(">>> CORREO: " + correo + " ROL: " + rol);

        UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(
            correo,
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + rol))
          );

        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
        System.out.println(">>> TOKEN INVÁLIDO");
      }
    } else {
      System.out.println(">>> SIN TOKEN O HEADER INCORRECTO");
    }

    filterChain.doFilter(request, response);
  }
}
