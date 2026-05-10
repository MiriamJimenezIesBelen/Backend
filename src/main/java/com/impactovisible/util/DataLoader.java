package com.impactovisible.util;

import com.impactovisible.domain.*;
import com.impactovisible.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private final EmpresaRepository empresaRepository;
  private final PlantaRepository plantaRepository;
  private final MedicionRepository medicionRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {

    if (empresaRepository.count() > 0) {
      log.info("Ya hay datos, saltando DataLoader.");
      return;
    }
    log.info("Iniciando carga de datos en ImpactoVisibleDB...");

    medicionRepository.deleteAllInBatch();
    plantaRepository.deleteAllInBatch();
    empresaRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
    userRepository.flush();

    // 1. EMPRESA ADMINISTRADORA
    Empresa adminEmpresa = Empresa.builder()
      .nombre("Admin Global")
      .numeroRegistro("ADM-001")
      .sector("Consultoría")
      .pais("España")
      .ciudad("Madrid")
      .tamano(Empresa.Tamano.grande)
      .correoContacto("admin@test.com")
      .password(passwordEncoder.encode("1234"))
      .rol(Empresa.Rol.ADMIN)
      .build();
    empresaRepository.save(adminEmpresa);
    log.info("Empresa ADMIN creada: admin@test.com / 1234");

    // 2. EMPRESA NORMAL — Tesla
    Empresa teslaEmpresa = Empresa.builder()
      .nombre("Tesla Motors")
      .numeroRegistro("REG-999-USA")
      .sector("Automotriz")
      .pais("EEUU")
      .ciudad("Austin")
      .tamano(Empresa.Tamano.mediana)
      .correoContacto("user@test.com")
      .password(passwordEncoder.encode("1234"))
      .rol(Empresa.Rol.USER)
      .build();
    empresaRepository.save(teslaEmpresa);
    log.info("Empresa USER creada: user@test.com / 1234");

    Planta gigaFactory = Planta.builder()
      .codigoPlanta("TX-01")
      .nombre("Giga Texas")
      .direccion("1 Tesla Road")
      .empresa(teslaEmpresa)
      .build();
    plantaRepository.save(gigaFactory);

    // 3. EMPRESA NORMAL — nueva empresa adicional
    Empresa segundaEmpresa = Empresa.builder()
      .nombre("GreenTech Solutions")
      .numeroRegistro("REG-002-ESP")
      .sector("Tecnología")
      .pais("España")
      .ciudad("Barcelona")
      .tamano(Empresa.Tamano.pequena)
      .correoContacto("greentech@test.com")
      .password(passwordEncoder.encode("1234"))
      .rol(Empresa.Rol.USER)
      .build();
    empresaRepository.save(segundaEmpresa);
    log.info("Empresa USER creada: greentech@test.com / 1234");

    Planta plantaBarcelona = Planta.builder()
      .codigoPlanta("BCN-01")
      .nombre("Planta Barcelona")
      .direccion("Av. Diagonal 100")
      .empresa(segundaEmpresa)
      .build();
    plantaRepository.save(plantaBarcelona);

    log.info("Carga de datos finalizada con éxito.");
  }
}
