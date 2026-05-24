package com.impactovisible.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "acciones_sostenibilidad")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "codigoAccion")
public class AccionSostenibilidad {

    @Id
    private String codigoAccion;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private EstadoAccion estado;


    public enum EstadoAccion { planificada, en_progreso, completada }
}
