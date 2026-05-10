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

import java.util.Arrays;

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
  public SecurityFilterChain filterChain(HttpSecurity http)
    throws Exception {

    http

      .cors(cors ->
        cors.configurationSource(
          corsConfigurationSource()
        )
      )

      .csrf(csrf -> csrf.disable())

      .sessionManagement(sess ->
        sess.sessionCreationPolicy(
          SessionCreationPolicy.STATELESS
        )
      )

      .authorizeHttpRequests(auth -> auth

        // PREFLIGHT
        .requestMatchers(
          CorsUtils::isPreFlightRequest
        ).permitAll()

        // LOGIN
        .requestMatchers(
          HttpMethod.POST,
          "/api/empresas/login"
        ).permitAll()

        // REGISTER
        .requestMatchers(
          HttpMethod.POST,
          "/api/empresas"
        ).permitAll()

        // PING
        .requestMatchers(
          HttpMethod.GET,
          "/api/empresas"
        ).permitAll()

        // RANKING
        .requestMatchers(
          "/api/ranking/**"
        ).permitAll()

        // TODO LO DEMAS
        .anyRequest().authenticated()
      )

      .addFilterBefore(
        jwtFilter,
        UsernamePasswordAuthenticationFilter.class
      );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
      new CorsConfiguration();

    configuration.setAllowedOrigins(
      Arrays.asList(
        "http://localhost:4200",
        "https://frontend-991y.onrender.com"
      )
    );

    configuration.setAllowedMethods(
      Arrays.asList(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "OPTIONS"
      )
    );

    configuration.setAllowedHeaders(
      Arrays.asList(
        "Authorization",
        "Content-Type",
        "Accept"
      )
    );

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
      "/**",
      configuration
    );

    return source;
  }
}
