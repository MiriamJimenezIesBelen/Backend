package com.impactovisible.controller;

import com.impactovisible.dto.MaquinaDTO;
import com.impactovisible.security.JwtService;
import com.impactovisible.service.MaquinaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/maquinas")
public class MaquinaController {
  private final MaquinaService maquinaService;
  private final JwtService jwtService;

  public MaquinaController(MaquinaService maquinaService, JwtService jwtService) {
    this.maquinaService = maquinaService;
    this.jwtService = jwtService;
  }

  @GetMapping("/")
  public ResponseEntity<List<MaquinaDTO>> getAll(
    @RequestHeader(value = "Authorization", required = false) String authHeader) {

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtService.isTokenValid(token)) {
        Long idEmpresa = jwtService.extractIdEmpresa(token);
        String rol = jwtService.extractRol(token);
        if ("ADMIN".equals(rol)) {
          return ResponseEntity.ok(maquinaService.findAll());
        }
        return ResponseEntity.ok(maquinaService.findByEmpresa(idEmpresa));
      }
    }

    return ResponseEntity.ok(maquinaService.findAll());
  }
}
