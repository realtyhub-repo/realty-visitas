package service.visitas.dto.internal;

import java.time.LocalDateTime;

/**
 * "codigo" es estable y pensado para que otros servicios (Lead) decidan qué hacer
 * sin tener que parsear el mensaje. Ej: AGENTE_OCUPADO, PROPIEDAD_OCUPADA.
 */
public record ErrorResponse(
        String codigo,
        String mensaje,
        int status,
        LocalDateTime timestamp
) {}
