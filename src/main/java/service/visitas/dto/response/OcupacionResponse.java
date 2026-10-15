package service.visitas.dto.response;

import java.time.LocalDateTime;

public record OcupacionResponse(
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin
) {}
