package com.impactovisible.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuración global de Spring MVC (no seguridad)
// aquí se define principalmente CORS a nivel de controlador
@Configuration
public class WebConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {

    return new WebMvcConfigurer() {

      @Override
      public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")

          // permite peticiones desde cualquier origen
          .allowedOrigins("*")

          // métodos HTTP permitidos
          .allowedMethods(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS"
          )

          // permite cualquier header en las peticiones
          .allowedHeaders("*");
      }
    };
  }
}
