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

// Filtro JWT que se ejecuta en cada request
// se encarga de leer el token y meter la autenticación en Spring Security
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  // Decide si este request se debe saltar el filtro
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    String path = request.getRequestURI();
    String method = request.getMethod();

    // debug: imprime cada ruta que pasa por el filtro
    System.out.println("FILTER CHECK: " + path);

    // dejar pasar preflight CORS (OPTIONS del navegador)
    if (method.equals("OPTIONS")) return true;

    // rutas públicas (no necesitan JWT)
    return path.contains("/api/empresas/login")
      || (path.equals("/api/empresas") && request.getMethod().equals("POST"))
      || path.startsWith("/api/ranking");
  }

  // lógica principal del filtro JWT
  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    // lee cabecera Authorization
    String authHeader = request.getHeader("Authorization");

    // debug básico
    System.out.println("=== JWT FILTER ===");
    System.out.println("URL: " + request.getRequestURI());
    System.out.println("Method: " + request.getMethod());
    System.out.println("Auth header: " + authHeader);

    // si viene token Bearer
    if (authHeader != null && authHeader.startsWith("Bearer ")) {

      // extrae el token sin "Bearer "
      String token = authHeader.substring(7);

      // valida token con servicio JWT
      boolean valid = jwtService.isTokenValid(token);

      System.out.println("Token válido: " + valid);

      if (valid) {

        // extrae datos del token
        String correo = jwtService.extractCorreo(token);
        String rol = jwtService.extractRol(token);

        System.out.println(
          "Correo: " + correo + " | Rol: " + rol
        );

        // crea autenticación en Spring Security context
        SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(
            correo,
            null,
            List.of(
              new SimpleGrantedAuthority("ROLE_" + rol)
            )
          )
        );
      }
    }

    // continúa la cadena de filtros
    filterChain.doFilter(request, response);
  }
}
