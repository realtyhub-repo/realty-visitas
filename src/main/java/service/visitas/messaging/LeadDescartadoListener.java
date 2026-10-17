package service.visitas.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import service.visitas.config.RabbitConfig;
import service.visitas.dto.event.LeadDescartadoEvent;
import service.visitas.service.VisitaService;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeadDescartadoListener {

    private final VisitaService visitaService;

    @RabbitListener(queues = RabbitConfig.QUEUE_LEAD_DESCARTADO)
    public void alDescartarLead(LeadDescartadoEvent evento) {
        visitaService.cancelarPorLead(evento.leadId());
        log.info("Lead {} descartado: visita programada cancelada si existía", evento.leadId());
    }
}
