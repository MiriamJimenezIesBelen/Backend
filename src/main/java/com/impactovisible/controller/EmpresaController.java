package com.impactovisible.controller;

import com.impactovisible.dto.EmpresaDTO;
import com.impactovisible.dto.LoginResponse;
import com.impactovisible.security.JwtService;
import com.impactovisible.service.EmpresaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/empresas")
public class EmpresaController {
  private final EmpresaService empresaService;
  private final JwtService jwtService;

  public EmpresaController(EmpresaService empresaService, JwtService jwtService) {
    this.empresaService = empresaService;
    this.jwtService = jwtService;
  }
  @GetMapping({"", "/"})
  public ResponseEntity<List<EmpresaDTO>> getAll() {
    return ResponseEntity.ok(empresaService.findAll());
  }

  @PostMapping({"", "/"})
  public ResponseEntity<EmpresaDTO> create(@RequestBody EmpresaDTO dto) {
    return ResponseEntity.ok(empresaService.save(dto));
  }


  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> datos) {
    LoginResponse response = empresaService.login(
      datos.get("correo"),
      datos.get("password")
    );
    return ResponseEntity.ok(response);
  }

  @PostMapping("/admin/crear")
  public ResponseEntity<EmpresaDTO> createFromAdmin(@RequestBody EmpresaDTO dto) {
    return ResponseEntity.ok(empresaService.save(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmpresaDTO> update(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
    return ResponseEntity.ok(empresaService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    empresaService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
