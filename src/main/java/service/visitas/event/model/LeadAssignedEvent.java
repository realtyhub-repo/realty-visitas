package service.visitas.event.model;

import lombok.Builder;
import service.visitas.entity.AsignacionHabilitada;

import java.util.UUID;

@Builder
public record LeadAssignedEvent(

    UUID leadId,
    UUID propiedadId,
    UUID clienteId,
    UUID agenteId
) {

}
