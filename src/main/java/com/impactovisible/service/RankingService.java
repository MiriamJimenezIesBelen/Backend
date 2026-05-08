package com.impactovisible.service;

import com.impactovisible.dto.RankingDTO;
import com.impactovisible.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

  private final EmpresaRepository empresaRepository;

  public List<RankingDTO> obtenerRanking() {

    return empresaRepository.findAll()
      .stream()
      .map(e -> new RankingDTO(
        e.getNombre(),
        calcularPuntos(e)   // <-- IMPORTANTE
      ))
      .sorted((a, b) -> Double.compare(b.getPuntos(), a.getPuntos()))
      .toList();
  }

  private Double calcularPuntos(Object empresa) {
    // AQUÍ PON TU LÓGICA REAL
    return 100.0; // temporal para test
  }
}
