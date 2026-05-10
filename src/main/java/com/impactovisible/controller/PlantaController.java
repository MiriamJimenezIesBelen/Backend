package com.impactovisible.controller;

import com.impactovisible.dto.PlantaDTO;
import com.impactovisible.security.JwtService;
import com.impactovisible.service.PlantaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/plantas")
public class PlantaController {
  private final PlantaService plantaService;
  private final JwtService jwtService;

  public PlantaController(PlantaService plantaService, JwtService jwtService) {
    this.plantaService = plantaService;
    this.jwtService = jwtService;
  }

  @GetMapping({"", "/"})
  public ResponseEntity<List<PlantaDTO>> getAll(
    @RequestHeader(value = "Authorization", required = false) String authHeader) {

    // Si viene token, devolvemos solo las plantas de esa empresa
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtService.isTokenValid(token)) {
        Long idEmpresa = jwtService.extractIdEmpresa(token);
        String rol = jwtService.extractRol(token);
        // ADMIN ve todo; USER solo ve las suyas
        if ("ADMIN".equals(rol)) {
          return ResponseEntity.ok(plantaService.findAll());
        }
        return ResponseEntity.ok(plantaService.findByEmpresa(idEmpresa));
      }
    }

    return ResponseEntity.ok(plantaService.findAll());
  }
}
