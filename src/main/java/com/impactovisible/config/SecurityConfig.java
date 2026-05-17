package com.impactovisible.config;

import com.impactovisible.security.JwtFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Configuración principal de seguridad de Spring Boot
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // Filtro JWT propio (valida token en cada request)
  private final JwtFilter jwtFilter;

  // Encoder de contraseñas (BCrypt)
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Cadena principal de seguridad (reglas HTTP)
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http)
    throws Exception {

    http

      // desactiva CSRF (no se usa en APIs REST con JWT)
      .csrf(csrf -> csrf.disable())

      // configuración CORS global
      .cors(cors ->
        cors.configurationSource(corsConfigurationSource())
      )

      // desactiva headers por defecto de seguridad (ajuste manual)
      .headers(headers -> headers.disable())

      // sesión sin estado (JWT -> no sesión en servidor)
      .sessionManagement(sess ->
        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )

      // reglas de autorización por endpoint
      .authorizeHttpRequests(auth -> auth

        // permite preflight CORS (OPTIONS del navegador)
        .requestMatchers(CorsUtils::isPreFlightRequest)
        .permitAll()

        // LOGIN público
        .requestMatchers(
          HttpMethod.POST,
          "/api/empresas/login"
        ).permitAll()

        // REGISTER público
        .requestMatchers(
          HttpMethod.POST,
          "/api/empresas"
        ).permitAll()

        // creación admin requiere login
        .requestMatchers(
          HttpMethod.POST,
          "/api/empresas/admin/crear"
        ).authenticated()

        // endpoint simple público
        .requestMatchers(
          HttpMethod.GET,
          "/api/empresas"
        ).permitAll()

        // ranking público
        .requestMatchers("/api/ranking/**")
        .permitAll()

        // CRUD empresas protegido
        .requestMatchers(HttpMethod.DELETE, "/api/empresas/**")
        .authenticated()

        .requestMatchers(HttpMethod.PUT, "/api/empresas/**")
        .authenticated()

        .requestMatchers(HttpMethod.GET, "/api/empresas/**")
        .authenticated()

        // cualquier otra ruta requiere login
        .anyRequest().authenticated()
      )

      // añade filtro JWT antes del filtro de login de Spring
      .addFilterBefore(
        jwtFilter,
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }

  // Configuración CORS (permite llamadas desde Angular)
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
      new CorsConfiguration();

    // permite cualquier origen (en producción se debería limitar)
    configuration.setAllowedOriginPatterns(
      List.of("*")
    );

    // métodos HTTP permitidos
    configuration.setAllowedMethods(
      List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
    );

    // headers permitidos
    configuration.setAllowedHeaders(
      List.of("*")
    );

    // no usar cookies/sesión
    configuration.setAllowCredentials(false);

    // cache de preflight (mejora rendimiento navegador)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();

    // aplica a todas las rutas
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
