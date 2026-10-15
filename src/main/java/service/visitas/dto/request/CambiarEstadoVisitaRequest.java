package service.visitas.dto.request;

import jakarta.validation.constraints.NotNull;
import service.visitas.entity.EstadoVisita;

public record CambiarEstadoVisitaRequest(
        @NotNull
        EstadoVisita nuevoEstado
) {}
