package com.impactovisible.dto;

public class RankingDTO {

  private String nombre;
  private Double puntos;

  public RankingDTO(String nombre, Double puntos) {
    this.nombre = nombre;
    this.puntos = puntos;
  }

  public String getNombre() {
    return nombre;
  }

  public Double getPuntos() {
    return puntos;
  }
}
