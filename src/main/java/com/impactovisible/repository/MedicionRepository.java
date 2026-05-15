package com.impactovisible.repository;

import com.impactovisible.domain.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, String> {
  @Query(value = """
    SELECT m.* FROM mediciones m
    LEFT JOIN plantas p ON m.codigo_planta = p.codigo_planta
    WHERE p.id_empresa = :idEmpresa
       OR m.id_empresa = :idEmpresa
    """, nativeQuery = true)
  List<Medicion> findByEmpresa_IdEmpresa(@Param("idEmpresa") Long idEmpresa);
}
