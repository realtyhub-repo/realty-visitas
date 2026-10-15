package service.visitas.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservarVisitaRequest(

        @NotNull
        UUID leadId,

        @NotNull
        UUID propiedadId,

        @NotNull
        UUID clienteId,

        @NotNull
        UUID agenteId,

        @NotNull
        LocalDateTime fechaHora
) {}
