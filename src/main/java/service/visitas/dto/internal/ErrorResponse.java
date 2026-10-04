package service.visitas.dto.internal;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensaje,
        int status,
        LocalDateTime timestamp
) {}