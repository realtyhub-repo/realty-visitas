package service.visitas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "AsignacionHabilitada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionHabilitada {

    @Id
    @Column(name = "lead_id")
    private UUID leadId;

    @Column(name = "propiedad_id", nullable = false)
    private UUID propiedadId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "agente_id",nullable = false)
    private UUID agenteId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

}
