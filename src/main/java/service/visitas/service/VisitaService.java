package service.visitas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.visitas.dto.internal.RolUsuario;
import service.visitas.dto.request.CrearVisitaRequest;
import service.visitas.dto.response.VisitaResponse;
import service.visitas.entity.AsignacionHabilitada;
import service.visitas.exceptions.AccesoNoAutorizadoException;
import service.visitas.repository.VisitaRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final AsignacionHabilitadaService habilitadaService;


    public VisitaResponse crear(UUID solicitanteId, RolUsuario rol, CrearVisitaRequest request){

        AsignacionHabilitada habilitada = habilitadaService.buscarPorLeadId(request.leadId());

        boolean esDueno =  habilitada.getAgenteId()!=null &&  habilitada.getAgenteId().equals(solicitanteId);
        boolean esAdmin =  rol==RolUsuario.ADMINISTRADOR_CENTRAL;

        if(!esAdmin && !esDueno)
            throw new AccesoNoAutorizadoException("No tienes permiso sobre este lead");

        LocalDateTime fechaHoraInicio = request.fechaHora().truncatedTo(ChronoUnit.MINUTES);
        long minutosDuracion = request.duracionMinutos()!=null?request.duracionMinutos():60L;
        LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(minutosDuracion);





        return new VisitaResponse();
    }


}
