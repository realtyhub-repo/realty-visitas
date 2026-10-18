package service.visitas.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisitaCanceladaEvent(
        UUID visitaId,
        UUID leadId,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin,
        UUID canceladaPor,
        MotivoCancelacion motivo
) {
}
