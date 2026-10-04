package service.visitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.visitas.entity.EstadoVisita;
import service.visitas.entity.Visita;

import java.time.LocalDateTime;
import java.util.UUID;

public interface VisitaRepository extends JpaRepository<Visita, UUID> {

    boolean existsByPropiedadIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            UUID propiedadId, EstadoVisita estado, LocalDateTime nuevoFin, LocalDateTime nuevoInicio);

    boolean existsByAgenteIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            UUID agenteId, EstadoVisita estado, LocalDateTime nuevoFin, LocalDateTime nuevoInicio
    );

}
