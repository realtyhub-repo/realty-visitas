package service.visitas.dto.response;

import lombok.Builder;
import service.visitas.entity.EstadoVisita;
import service.visitas.entity.Visita;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record VisitaResponse(
        UUID id,
        UUID propiedadId,
        UUID clienteId,
        UUID agenteId,
        String nombreAgente,
        UUID leadId,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin,
        EstadoVisita estadoVisita
) {
    public static VisitaResponse from(Visita visita) {
        return from(visita, null);
    }

    public static VisitaResponse from(Visita visita, String nombreAgente) {
        return VisitaResponse.builder()
                .id(visita.getId())
                .propiedadId(visita.getPropiedadId())
                .clienteId(visita.getClienteId())
                .agenteId(visita.getAgenteId())
                .nombreAgente(nombreAgente)
                .leadId(visita.getLeadId())
                .fechaHora(visita.getFechaHora())
                .fechaHoraFin(visita.getFechaHoraFin())
                .estadoVisita(visita.getEstadoVisita())
                .build();
    }
}
