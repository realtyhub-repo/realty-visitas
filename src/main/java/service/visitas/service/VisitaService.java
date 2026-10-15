package service.visitas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.visitas.config.VisitasProperties;
import service.visitas.dto.internal.RolUsuario;
import service.visitas.dto.request.ReservarVisitaRequest;
import service.visitas.dto.response.AgentesOcupadosResponse;
import service.visitas.dto.response.OcupacionResponse;
import service.visitas.dto.response.VisitaResponse;
import service.visitas.entity.EstadoVisita;
import service.visitas.entity.Visita;
import service.visitas.exceptions.*;
import service.visitas.repository.VisitaRepository;
import service.visitas.security.ContextoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitaService {

    // Colombia es UTC-5 sin horario de verano. Así "ahora" no depende de la zona del servidor.
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final int DIAS_POR_DEFECTO = 30;

    private static final String CONSTRAINT_AGENTE_SIN_SOLAPAMIENTO    = "ex_visita_agente_sin_solapamiento";
    private static final String CONSTRAINT_PROPIEDAD_SIN_SOLAPAMIENTO = "ex_visita_propiedad_sin_solapamiento";
    private static final String INDICE_LEAD_PROGRAMADA                = "uk_visita_lead_programada";

    private final VisitaRepository visitaRepository;
    private final VisitasProperties visitasProperties;

    // ------------------------------------------------------------------
    // INTERNO: qué agentes de la lista están ocupados a esa hora (la usa Lead)
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public AgentesOcupadosResponse consultarAgentesOcupados(List<UUID> agenteIds, LocalDateTime fechaHora) {

        if (agenteIds == null || agenteIds.isEmpty()) {
            return AgentesOcupadosResponse.builder()
                    .agentesIds(List.of())
                    .build();
        }

        LocalDateTime inicio = normalizar(fechaHora);
        validarSlot(inicio);
        LocalDateTime fin = calcularFin(inicio);

        List<UUID> agentesOcupados = visitaRepository.findAgenteIdsOcupados(
                agenteIds, EstadoVisita.CANCELADA, inicio, fin);

        return AgentesOcupadosResponse.builder()
                .agentesIds(agentesOcupados)
                .build();
    }

    // ------------------------------------------------------------------
    // INTERNO: reservar el slot (la usa Lead al crear el lead o al pedir otra visita)
    // ------------------------------------------------------------------
    @Transactional
    public VisitaResponse reservar(ReservarVisitaRequest request) {

        LocalDateTime inicio = normalizar(request.fechaHora());
        validarSlot(inicio);
        LocalDateTime fin = calcularFin(inicio);

        // Pre-chequeos: dan mensajes claros en el caso normal.
        // La defensa real contra carreras son los constraints de la BD (ver el catch).
        if (visitaRepository.existsByLeadIdAndEstadoVisita(request.leadId(), EstadoVisita.PROGRAMADA)) {
            throw new VisitaYaExisteException("Este lead ya tiene una visita programada");
        }

        // La propiedad va primero: si está ocupada no tiene sentido probar con otro agente.
        if (visitaRepository.existsByPropiedadIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                request.propiedadId(), EstadoVisita.CANCELADA, fin, inicio)) {
            throw new PropiedadOcupadaException("La propiedad ya tiene una visita en ese horario");
        }

        if (visitaRepository.existsByAgenteIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                request.agenteId(), EstadoVisita.CANCELADA, fin, inicio)) {
            throw new AgenteOcupadoException("El agente ya tiene una visita en ese horario");
        }

        Visita visita = Visita.builder()
                .leadId(request.leadId())
                .propiedadId(request.propiedadId())
                .clienteId(request.clienteId())
                .agenteId(request.agenteId())
                .fechaHora(inicio)
                .fechaHoraFin(fin)
                .estadoVisita(EstadoVisita.PROGRAMADA)
                .build();

        try {
            // saveAndFlush para que el constraint dispare aquí y no al cerrar la transacción
            return VisitaResponse.from(visitaRepository.saveAndFlush(visita));
        } catch (DataIntegrityViolationException e) {
            throw traducirViolacion(e);
        }
    }

    // ------------------------------------------------------------------
    // INTERNO: compensación si Lead falla después de reservar. Idempotente.
    // ------------------------------------------------------------------
    @Transactional
    public void cancelarPorLead(UUID leadId) {
        visitaRepository.findByLeadIdAndEstadoVisita(leadId, EstadoVisita.PROGRAMADA)
                .ifPresent(visita -> {
                    visita.setEstadoVisita(EstadoVisita.CANCELADA);
                    visitaRepository.save(visita);
                });
        // Si no hay visita pendiente, no es un error.
    }

    // ------------------------------------------------------------------
    // PÚBLICO: ocupación de una propiedad en un día (calendario del front)
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<OcupacionResponse> obtenerOcupacionPropiedad(UUID propiedadId, LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

        // Solo inicio y fin: nada de cliente ni agente.
        return visitaRepository
                .findByPropiedadIdAndEstadoVisitaNotAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHora(
                        propiedadId, EstadoVisita.CANCELADA, desde, hasta)
                .stream()
                .map(v -> new OcupacionResponse(v.getFechaHora(), v.getFechaHoraFin()))
                .toList();
    }

    // ------------------------------------------------------------------
    // Mis visitas: el CLIENTE ve las suyas; cualquier otro rol ve las que tiene como agente
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<VisitaResponse> listarMisVisitas(ContextoUsuario contexto, EstadoVisita estado,
                                                 LocalDateTime desde, LocalDateTime hasta) {

        LocalDateTime desdeEfectivo = desde != null ? desde : ahora().toLocalDate().atStartOfDay();
        LocalDateTime hastaEfectivo = hasta != null ? hasta : desdeEfectivo.plusDays(DIAS_POR_DEFECTO);

        if (desdeEfectivo.isAfter(hastaEfectivo)) {
            throw new FechaInvalidaException("El rango de fechas es inválido");
        }

        UUID userId = contexto.userId();
        List<Visita> visitas;

        if (contexto.rol() == RolUsuario.CLIENTE) {
            visitas = (estado == null)
                    ? visitaRepository.findByClienteIdAndFechaHoraBetweenOrderByFechaHora(
                            userId, desdeEfectivo, hastaEfectivo)
                    : visitaRepository.findByClienteIdAndEstadoVisitaAndFechaHoraBetweenOrderByFechaHora(
                            userId, estado, desdeEfectivo, hastaEfectivo);
        } else {
            visitas = (estado == null)
                    ? visitaRepository.findByAgenteIdAndFechaHoraBetweenOrderByFechaHora(
                            userId, desdeEfectivo, hastaEfectivo)
                    : visitaRepository.findByAgenteIdAndEstadoVisitaAndFechaHoraBetweenOrderByFechaHora(
                            userId, estado, desdeEfectivo, hastaEfectivo);
        }

        return visitas.stream().map(VisitaResponse::from).toList();
    }

    // ------------------------------------------------------------------
    // REALIZADA: agente dueño o admin. CANCELADA: agente dueño, admin o el cliente de la visita.
    // ------------------------------------------------------------------
    @Transactional
    public VisitaResponse cambiarEstado(UUID visitaId, EstadoVisita nuevoEstado, ContextoUsuario contexto) {

        Visita visita = visitaRepository.findById(visitaId)
                .orElseThrow(() -> new VisitaNoEncontradaException("Visita no encontrada"));

        // Autorización primero, antes de revelar nada del estado de la visita
        boolean esAgenteDueno = visita.getAgenteId().equals(contexto.userId());
        boolean esAdmin = contexto.rol() == RolUsuario.ADMINISTRADOR_CENTRAL;
        boolean clienteCancelaLaSuya = nuevoEstado == EstadoVisita.CANCELADA
                && visita.getClienteId().equals(contexto.userId());

        if (!esAgenteDueno && !esAdmin && !clienteCancelaLaSuya) {
            throw new AccesoNoAutorizadoException("No tienes permiso sobre esta visita");
        }

        // Solo se sale de PROGRAMADA, y nunca hacia PROGRAMADA
        if (visita.getEstadoVisita() != EstadoVisita.PROGRAMADA || nuevoEstado == EstadoVisita.PROGRAMADA) {
            throw new TransicionInvalidaException(
                    "No se puede pasar de " + visita.getEstadoVisita() + " a " + nuevoEstado);
        }

        if (nuevoEstado == EstadoVisita.REALIZADA && visita.getFechaHora().isAfter(ahora())) {
            throw new TransicionInvalidaException("No puedes marcar como realizada una visita que aún no ocurre");
        }

        visita.setEstadoVisita(nuevoEstado);
        // TODO: publicar visita.realizada / visita.cancelada por Rabbit cuando otro servicio lo necesite
        return VisitaResponse.from(visitaRepository.save(visita));
    }

    // ==================================================================
    // Helpers
    // ==================================================================

    private LocalDateTime ahora() {
        return LocalDateTime.now(ZONA).truncatedTo(ChronoUnit.MINUTES);
    }

    private LocalDateTime normalizar(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new FechaInvalidaException("La fecha y hora de la visita son obligatorias");
        }
        // Solo se trunca el inicio: el fin sale de sumarle minutos enteros.
        return fechaHora.truncatedTo(ChronoUnit.MINUTES);
    }

    private LocalDateTime calcularFin(LocalDateTime inicio) {
        return inicio.plusMinutes(visitasProperties.duracionMinutos());
    }

    private void validarSlot(LocalDateTime inicio) {
        LocalDateTime fin = calcularFin(inicio);

        // La fecha debe ser futura
        if (!inicio.isAfter(ahora())) {
            throw new FechaInvalidaException("La fecha de la visita debe ser futura");
        }

        // Inicio y fin deben caer el mismo día
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new FueraDeHorarioException("El inicio y el fin de la visita deben ser el mismo día");
        }

        LocalTime apertura = visitasProperties.horaApertura();
        LocalTime cierre = visitasProperties.horaCierre();

        if (inicio.toLocalTime().isBefore(apertura)) {
            throw new FueraDeHorarioException("La hora de inicio es anterior a la apertura (" + apertura + ")");
        }

        if (fin.toLocalTime().isAfter(cierre)) {
            throw new FueraDeHorarioException("La visita terminaría después del cierre (" + cierre + ")");
        }
    }

    private RuntimeException traducirViolacion(DataIntegrityViolationException e) {
        String mensaje = NestedExceptionUtils.getMostSpecificCause(e).getMessage();

        if (mensaje != null) {
            if (mensaje.contains(CONSTRAINT_AGENTE_SIN_SOLAPAMIENTO)) {
                return new AgenteOcupadoException("El agente ya tiene una visita en ese horario");
            }
            if (mensaje.contains(CONSTRAINT_PROPIEDAD_SIN_SOLAPAMIENTO)) {
                return new PropiedadOcupadaException("La propiedad ya tiene una visita en ese horario");
            }
            if (mensaje.contains(INDICE_LEAD_PROGRAMADA)) {
                return new VisitaYaExisteException("Este lead ya tiene una visita programada");
            }
        }
        return e; // otro tipo de violación: que suba tal cual
    }
}
