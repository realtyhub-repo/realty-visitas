package service.visitas.event.listener;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import service.visitas.config.RabbitConfig;
import service.visitas.event.model.LeadAssignedEvent;
import service.visitas.service.AsignacionHabilitadaService;

@Component
@RequiredArgsConstructor
public class LeadAssignedListener {


    private final AsignacionHabilitadaService asignacionHabilitadaService;

    @RabbitListener(queues = RabbitConfig.NAME_QUEUE)
    public void leadAssignedListenerEvent(LeadAssignedEvent event){

        asignacionHabilitadaService.registrar(event);
    }


}
