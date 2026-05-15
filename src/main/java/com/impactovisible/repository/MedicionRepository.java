package com.impactovisible.repository;

import com.impactovisible.domain.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, String> {
  @Query("SELECT m FROM Medicion m WHERE m.planta.empresa.idEmpresa = :idEmpresa")
  List<Medicion> findByEmpresa_IdEmpresa(@Param("idEmpresa") Long idEmpresa);}
