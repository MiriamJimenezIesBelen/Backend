package com.impactovisible.controller;

import com.impactovisible.dto.MedicionDTO;
import com.impactovisible.service.MedicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/mediciones")
public class MedicionController {

  private final MedicionService service;

  public MedicionController(MedicionService service) {
    this.service = service;
  }

  @GetMapping("/")
  public ResponseEntity<List<MedicionDTO>> getAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @PostMapping
  public ResponseEntity<Void> guardar(@RequestBody MedicionDTO dto) {
    service.guardarDesdeFormulario(dto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/empresa/{idEmpresa}")
  public ResponseEntity<List<MedicionDTO>> getByEmpresa(@PathVariable Long idEmpresa) {
    return ResponseEntity.ok(service.findByEmpresa(idEmpresa));
  }

  @DeleteMapping("/empresa/{idEmpresa}/fecha/{fecha}")
  public ResponseEntity<Void> eliminarPorFecha(
    @PathVariable Long idEmpresa,
    @PathVariable String fecha) {
    service.eliminarPorFecha(idEmpresa, fecha);
    return ResponseEntity.noContent().build();
  }
}
