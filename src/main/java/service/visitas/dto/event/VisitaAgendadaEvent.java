package service.visitas.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitaAgendadaEvent(
        UUID visitaId,
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin
) {
}
