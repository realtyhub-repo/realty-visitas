package service.visitas.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import service.visitas.config.RabbitConfig;
import service.visitas.dto.event.MotivoCancelacion;
import service.visitas.dto.event.VisitaAgendadaEvent;
import service.visitas.dto.event.VisitaCanceladaEvent;
import service.visitas.dto.event.VisitaRealizadaEvent;
import service.visitas.entity.Visita;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarAgendada(Visita visita) {
        VisitaAgendadaEvent evento = new VisitaAgendadaEvent(
                visita.getId(),
                visita.getLeadId(),
                visita.getPropiedadId(),
                visita.getClienteId(),
                visita.getAgenteId(),
                visita.getFechaHora(),
                visita.getFechaHoraFin());
        enviarTrasCommit(RabbitConfig.RK_VISITA_AGENDADA, evento);
    }

    public void publicarCancelada(Visita visita, UUID canceladaPor, MotivoCancelacion motivo) {
        VisitaCanceladaEvent evento = new VisitaCanceladaEvent(
                visita.getId(),
                visita.getLeadId(),
                visita.getPropiedadId(),
                visita.getClienteId(),
                visita.getAgenteId(),
                visita.getFechaHora(),
                visita.getFechaHoraFin(),
                canceladaPor,
                motivo);
        enviarTrasCommit(RabbitConfig.RK_VISITA_CANCELADA, evento);
    }

    public void publicarRealizada(Visita visita) {
        VisitaRealizadaEvent evento = new VisitaRealizadaEvent(
                visita.getId(),
                visita.getLeadId(),
                visita.getPropiedadId(),
                visita.getClienteId(),
                visita.getAgenteId(),
                visita.getFechaHora(),
                visita.getFechaHoraFin());
        enviarTrasCommit(RabbitConfig.RK_VISITA_REALIZADA, evento);
    }

    private void enviarTrasCommit(String routingKey, Object evento) {
        Runnable envio = () -> {
            try {
                rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, evento);
            } catch (AmqpException e) {
                log.error("No se pudo publicar {}: {}", routingKey, e.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    envio.run();
                }
            });
        } else {
            envio.run();
        }
    }
}
