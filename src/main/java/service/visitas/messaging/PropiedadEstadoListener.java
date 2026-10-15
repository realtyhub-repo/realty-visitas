package service.visitas.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import service.visitas.config.RabbitConfig;
import service.visitas.dto.event.EstadoComercial;
import service.visitas.dto.event.PropiedadEstadoCambiadoEvent;
import service.visitas.service.VisitaService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PropiedadEstadoListener {

    private final VisitaService visitaService;

    @RabbitListener(queues = RabbitConfig.QUEUE_PROPIEDAD_ESTADO_CAMBIADO)
    public void alCambiarEstadoPropiedad(PropiedadEstadoCambiadoEvent evento) {
        boolean yaNoSeVisita = evento.estadoNuevo() == EstadoComercial.VENDIDO
                || evento.estadoNuevo() == EstadoComercial.ALQUILADO;

        if (!yaNoSeVisita) {
            return;
        }

        int canceladas = visitaService.cancelarPendientesPorPropiedad(evento.propiedadId());
        log.info("Propiedad {} pasó a {}: {} visitas canceladas",
                evento.propiedadId(), evento.estadoNuevo(), canceladas);
    }
}
