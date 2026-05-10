package com.impactovisible.repository;

import com.impactovisible.domain.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, String> {
  List<Zona> findByPlanta_Empresa_IdEmpresa(Long idEmpresa);
}
