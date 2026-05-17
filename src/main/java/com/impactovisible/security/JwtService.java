package com.impactovisible.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Servicio encargado de crear, leer y validar JWT
@Service
public class JwtService {

  // clave secreta usada para firmar el token
  @Value("${jwt.secret}")
  private String secret;

  // tiempo de expiración del token (ms)
  @Value("${jwt.expiration}")
  private long expiration;

  // convierte la clave secreta en una Key segura para HS256
  private SecretKey getKey() {

    byte[] keyBytes = secret.getBytes();

    return Keys.hmacShaKeyFor(keyBytes);
  }

  // genera el token JWT al hacer login correcto
  public String generateToken(
    String correo,
    String rol,
    Long idEmpresa
  ) {

    // datos adicionales dentro del token (payload)
    Map<String, Object> claims = new HashMap<>();

    claims.put("rol", rol);
    claims.put("idEmpresa", idEmpresa);

    // construcción del JWT
    return Jwts.builder()

      // datos personalizados
      .setClaims(claims)

      // usuario principal del token
      .setSubject(correo)

      // fecha de creación
      .setIssuedAt(new Date())

      // fecha de expiración
      .setExpiration(
        new Date(System.currentTimeMillis() + expiration)
      )

      // firma del token (HS256 + clave secreta)
      .signWith(getKey(), SignatureAlgorithm.HS256)

      .compact();
  }

  // extrae el correo (subject del token)
  public String extractCorreo(String token) {
    return getClaims(token).getSubject();
  }

  // extrae rol del payload
  public String extractRol(String token) {
    return getClaims(token).get("rol", String.class);
  }

  // extrae id de empresa del payload
  public Long extractIdEmpresa(String token) {
    return getClaims(token).get("idEmpresa", Long.class);
  }

  // valida si el token es correcto y no está expirado
  public boolean isTokenValid(String token) {

    try {

      getClaims(token);

      return true;

    } catch (JwtException | IllegalArgumentException e) {

      return false;
    }
  }

  // método interno: decodifica y valida el JWT
  private Claims getClaims(String token) {

    return Jwts.parserBuilder()

      .setSigningKey(getKey())

      .build()

      .parseClaimsJws(token)

      .getBody();
  }
}
