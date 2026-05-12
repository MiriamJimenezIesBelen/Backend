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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
      .csrf(csrf -> csrf.disable())

      .cors(cors -> cors.configurationSource(corsConfigurationSource()))

      .sessionManagement(sess ->
        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )

      .authorizeHttpRequests(auth -> auth

        // Preflight CORS
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

        // LOGIN
        .requestMatchers(HttpMethod.POST, "/api/empresas/login").permitAll()

        // REGISTRO PÚBLICO (formulario de alta de nuevas empresas)
        .requestMatchers(HttpMethod.POST, "/api/empresas", "/api/empresas/").permitAll()

        // LISTADO (usado por el frontend para comprobar si el backend responde)
        .requestMatchers(HttpMethod.GET, "/api/empresas", "/api/empresas/").permitAll()

        // RANKING
        .requestMatchers("/api/ranking/**").permitAll()

        // Todo lo demás requiere token válido
        .anyRequest().authenticated()
      )

      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(List.of("*"));

    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    configuration.setAllowedHeaders(List.of("*"));

    // CORREGIDO: false para no exigir credenciales en CORS (compatible con allowedOriginPatterns(*))
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
