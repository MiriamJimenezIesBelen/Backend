package com.impactovisible.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "plantas")
@EqualsAndHashCode(of = "idEmpresa")
public class Empresa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_empresa")
  private Long idEmpresa;

  @Column(name = "numero_registro")
  private String numeroRegistro;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "password")
  private String password;

  @Column(name = "sector")
  private String sector;

  @Column(name = "pais")
  private String pais;

  @Column(name = "ciudad")
  private String ciudad;

  @Enumerated(EnumType.STRING)
  @Column(name = "tamano")
  private Tamano tamano;

  @Column(name = "correo_contacto", unique = true, nullable = false)
  private String correoContacto;

  @Enumerated(EnumType.STRING)
  @Column(name = "rol")
  private Rol rol;

  @OneToMany(mappedBy = "empresa")
  private List<Planta> plantas;

  public enum Tamano { pequena, mediana, grande }

  public enum Rol { USER, ADMIN }
}
