package service.visitas.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PropiedadEstadoCambiadoEvent(
        UUID propiedadId,
        EstadoComercial estadoAnterior,
        EstadoComercial estadoNuevo,
        LocalDateTime ocurridoEn
) {
}
