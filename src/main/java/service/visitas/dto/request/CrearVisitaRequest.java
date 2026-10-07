package service.visitas.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CrearVisitaRequest(

        @NotNull
        UUID leadId,

        @NotNull
        @Future
        LocalDateTime fechaHora,

        @Min(15)
        @Max(240)
        Long duracionMinutos


) {
}
