package com.impactovisible.controller;

import com.impactovisible.dto.ZonaDTO;
import com.impactovisible.security.JwtService;
import com.impactovisible.service.ZonaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/zonas")
public class ZonaController {
  private final ZonaService zonaService;
  private final JwtService jwtService;

  public ZonaController(ZonaService zonaService, JwtService jwtService) {
    this.zonaService = zonaService;
    this.jwtService = jwtService;
  }

  @GetMapping({"", "/"})
  public ResponseEntity<List<ZonaDTO>> getAll(
    @RequestHeader(value = "Authorization", required = false) String authHeader) {

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtService.isTokenValid(token)) {
        Long idEmpresa = jwtService.extractIdEmpresa(token);
        String rol = jwtService.extractRol(token);
        if ("ADMIN".equals(rol)) {
          return ResponseEntity.ok(zonaService.findAll());
        }
        return ResponseEntity.ok(zonaService.findByEmpresa(idEmpresa));
      }
    }

    return ResponseEntity.ok(zonaService.findAll());
  }
}
