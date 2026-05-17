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

// Controlador REST de empresas
// expone endpoints de CRUD y login
@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/empresas")
public class EmpresaController {

  private final EmpresaService empresaService;
  private final JwtService jwtService;

  public EmpresaController(
    EmpresaService empresaService,
    JwtService jwtService
  ) {
    this.empresaService = empresaService;
    this.jwtService = jwtService;
  }

  // Obtener todas las empresas
  @GetMapping({"", "/"})
  public ResponseEntity<List<EmpresaDTO>> getAll() {

    return ResponseEntity.ok(
      empresaService.findAll()
    );
  }

  // Crear empresa (registro público)
  @PostMapping({"", "/"})
  public ResponseEntity<EmpresaDTO> create(
    @RequestBody EmpresaDTO dto
  ) {

    return ResponseEntity.ok(
      empresaService.save(dto)
    );
  }

  // Login de empresa
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(
    @RequestBody Map<String, String> datos
  ) {

    LoginResponse response =
      empresaService.login(
        datos.get("correo"),
        datos.get("password")
      );

    return ResponseEntity.ok(response);
  }

  // Crear empresa desde panel admin
  @PostMapping("/admin/crear")
  public ResponseEntity<EmpresaDTO> createFromAdmin(
    @RequestBody EmpresaDTO dto
  ) {

    return ResponseEntity.ok(
      empresaService.save(dto)
    );
  }

  // Actualizar empresa por ID
  @PutMapping("/{id}")
  public ResponseEntity<EmpresaDTO> update(
    @PathVariable Long id,
    @RequestBody EmpresaDTO dto
  ) {

    return ResponseEntity.ok(
      empresaService.update(id, dto)
    );
  }

  // Eliminar empresa (DELETE normal)
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable Long id
  ) {

    empresaService.delete(id);

    return ResponseEntity.noContent().build();
  }

  // Eliminar empresa usando POST (alternativa por proxies o bloqueos CORS)
  @PostMapping("/{id}/eliminar")
  public ResponseEntity<Void> deletePost(
    @PathVariable Long id
  ) {

    empresaService.delete(id);

    return ResponseEntity.noContent().build();
  }
}
