package com.impactovisible.service;

import com.impactovisible.domain.*;
import com.impactovisible.dto.MedicionDTO;
import com.impactovisible.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicionService {

  private final MedicionRepository medicionRepository;
  private final EmpresaRepository empresaRepository;

  public MedicionService(MedicionRepository medicionRepository,
                         EmpresaRepository empresaRepository) {
    this.medicionRepository = medicionRepository;
    this.empresaRepository = empresaRepository;
  }

  // =========================================
  // Guarda mediciones desde el formulario
  // (energía, agua, CO2, residuos)
  // =========================================
  public void guardarDesdeFormulario(MedicionDTO dto) {

    // Buscar empresa asociada al DTO
    Empresa empresa = empresaRepository.findById(dto.getIdEmpresa())
      .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

    // Fecha actual del registro
    LocalDate fecha = LocalDate.now();

    // Código base aleatorio para agrupar mediciones
    String base = UUID.randomUUID().toString().substring(0, 8);

    // Guardar solo si vienen valores no nulos
    if (dto.getEnergia() != null)
      guardar(empresa, fecha, Medicion.TipoMedicion.electricidad, dto.getEnergia(), base + "-E");

    if (dto.getAgua() != null)
      guardar(empresa, fecha, Medicion.TipoMedicion.agua, dto.getAgua(), base + "-A");

    if (dto.getCo2() != null)
      guardar(empresa, fecha, Medicion.TipoMedicion.emision, dto.getCo2(), base + "-C");

    if (dto.getResiduos() != null)
      guardar(empresa, fecha, Medicion.TipoMedicion.residuo, dto.getResiduos(), base + "-R");
  }

  // =========================================
  // Método interno para persistir una medición
  // =========================================
  private void guardar(Empresa empresa,
                       LocalDate fecha,
                       Medicion.TipoMedicion tipo,
                       BigDecimal valor,
                       String codigo) {

    medicionRepository.save(
      Medicion.builder()
        .codigoMedicion(codigo)
        .empresa(empresa)
        .fecha(fecha)
        .tipo(tipo)
        .valor(valor)
        .build()
    );
  }

  // =========================================
  // Obtener mediciones agrupadas por empresa
  // (formato listo para gráficos frontend)
  // =========================================
  public List<MedicionDTO> findByEmpresa(Long idEmpresa) {

    // Obtener todas las mediciones de la empresa
    List<Medicion> todas =
      medicionRepository.findByEmpresa_IdEmpresa(idEmpresa);

    return todas.stream()

      // Agrupar por fecha (cada día = un bloque de datos)
      .collect(Collectors.groupingBy(Medicion::getFecha))

      // Convertir el Map en stream ordenado por fecha
      .entrySet().stream()
      .sorted(Map.Entry.comparingByKey())

      // Transformar cada grupo en un DTO
      .map(entry -> {

        List<Medicion> grupo = entry.getValue();

        return MedicionDTO.builder()
          .fecha(entry.getKey())

          // Extraer valores por tipo de medición
          .energia(getValor(grupo, Medicion.TipoMedicion.electricidad))
          .agua(getValor(grupo, Medicion.TipoMedicion.agua))
          .co2(getValor(grupo, Medicion.TipoMedicion.emision))
          .residuos(getValor(grupo, Medicion.TipoMedicion.residuo))

          .build();
      })

      .collect(Collectors.toList());
  }

  // =========================================
  // Extrae valor de un tipo concreto dentro de un grupo
  // =========================================
  private BigDecimal getValor(List<Medicion> grupo,
                              Medicion.TipoMedicion tipo) {

    return grupo.stream()
      .filter(m -> m.getTipo() == tipo)
      .map(Medicion::getValor)
      .findFirst()
      .orElse(BigDecimal.ZERO);
  }

  // =========================================
  // Devuelve todas las mediciones sin agrupar
  // =========================================
  public List<MedicionDTO> findAll() {

    return medicionRepository.findAll().stream()
      .map(m -> MedicionDTO.builder()
        .codigoMedicion(m.getCodigoMedicion())
        .tipo(m.getTipo().name())
        .valor(m.getValor())
        .fecha(m.getFecha())
        .build()
      )
      .collect(Collectors.toList());
  }

  // =========================================
  // Elimina mediciones de una empresa por fecha
  // =========================================
  public void eliminarPorFecha(Long idEmpresa, String fecha) {

    LocalDate localDate = LocalDate.parse(fecha);

    List<Medicion> mediciones =
      medicionRepository.findByEmpresa_IdEmpresa(idEmpresa);

    mediciones.stream()
      .filter(m -> m.getFecha().equals(localDate))
      .forEach(m -> medicionRepository.delete(m));
  }
}
