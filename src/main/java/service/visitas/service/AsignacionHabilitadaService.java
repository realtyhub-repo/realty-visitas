package service.visitas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.visitas.entity.AsignacionHabilitada;
import service.visitas.event.model.LeadAssignedEvent;
import service.visitas.exceptions.AsignacionNoEncontradaException;
import service.visitas.repository.AsignacionHabilitadaRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AsignacionHabilitadaService {

    private final AsignacionHabilitadaRepository habilitadaRepository;

    @Transactional
    public void registrar(LeadAssignedEvent event){
       Optional<AsignacionHabilitada> asignacionHabilitada = habilitadaRepository.findById(event.leadId());

       if(asignacionHabilitada.isPresent()){
            AsignacionHabilitada habilitada = asignacionHabilitada.get();
            habilitada.setPropiedadId(event.propiedadId());
            habilitada.setClienteId(event.clienteId());
            habilitada.setAgenteId(event.agenteId());
            habilitadaRepository.save(habilitada);
            return;
       }

       AsignacionHabilitada asignacionNueva = AsignacionHabilitada.builder()
               .leadId(event.leadId())
               .propiedadId(event.propiedadId())
               .clienteId(event.clienteId())
               .agenteId(event.agenteId())
               .build();

       habilitadaRepository.save(asignacionNueva);
    }


    public AsignacionHabilitada buscarPorLeadId(UUID leadId){

        return habilitadaRepository.findById(leadId).orElseThrow(()->
                new AsignacionNoEncontradaException("El lead no tiene una asignación habilitada")
                );

    }

}


