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

        // Que sea futura se valida en VisitaService con la hora de Colombia,
        // no con @Future, que usaría la zona horaria del servidor.
        @NotNull
        LocalDateTime fechaHora
) {}
