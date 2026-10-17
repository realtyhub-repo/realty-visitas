package service.visitas.dto.event;

import java.util.UUID;

public record LeadDescartadoEvent(
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId
) {
}
