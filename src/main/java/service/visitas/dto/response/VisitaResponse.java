package service.visitas.dto.response;

import lombok.Builder;
import service.visitas.entity.EstadoVisita;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VisitaResponse(

        UUID id,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        UUID leadId,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin,
        EstadoVisita estado

) {

}
