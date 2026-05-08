package com.impactovisible.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "objetivos_esg")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObjetivoESG {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long idEmpresa;
  private String tipo;
  private String direccion;   // ← AÑADIR ESTO

  private Double valorObjetivo;
  private Double valorActual;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaInicio;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate fechaFin;

  private boolean activo = true;

  private String periodo;
}
