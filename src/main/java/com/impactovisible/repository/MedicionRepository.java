package com.impactovisible.repository;

import com.impactovisible.domain.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, String> {
  List<Medicion> findByEmpresa_IdEmpresa(Long idEmpresa);
}
