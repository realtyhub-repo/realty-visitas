package service.visitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.visitas.entity.EstadoVisita;
import service.visitas.entity.Visita;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitaRepository extends JpaRepository<Visita, UUID> {

    boolean existsByPropiedadIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            UUID propiedadId, EstadoVisita estado, LocalDateTime nuevoFin, LocalDateTime nuevoInicio);

    boolean existsByAgenteIdAndEstadoVisitaNotAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            UUID agenteId, EstadoVisita estado, LocalDateTime nuevoFin, LocalDateTime nuevoInicio);

    @Query("""
        select distinct v.agenteId from Visita v
        where v.agenteId in :agenteIds
          and v.estadoVisita <> :excluido
          and v.fechaHora < :fin
          and v.fechaHoraFin > :inicio
        """)
    List<UUID> findAgenteIdsOcupados(@Param("agenteIds") Collection<UUID> agenteIds,
                                     @Param("excluido") EstadoVisita excluido,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin);

    List<Visita> findByPropiedadIdAndEstadoVisitaNotAndFechaHoraGreaterThanEqualAndFechaHoraLessThanOrderByFechaHora(
            UUID propiedadId, EstadoVisita excluido, LocalDateTime desde, LocalDateTime hasta);

    List<Visita> findByAgenteIdAndFechaHoraBetweenOrderByFechaHora(
            UUID agenteId, LocalDateTime desde, LocalDateTime hasta);

    List<Visita> findByAgenteIdAndEstadoVisitaAndFechaHoraBetweenOrderByFechaHora(
            UUID agenteId, EstadoVisita estado, LocalDateTime desde, LocalDateTime hasta);

    List<Visita> findByClienteIdAndFechaHoraBetweenOrderByFechaHora(
            UUID clienteId, LocalDateTime desde, LocalDateTime hasta);

    List<Visita> findByClienteIdAndEstadoVisitaAndFechaHoraBetweenOrderByFechaHora(
            UUID clienteId, EstadoVisita estado, LocalDateTime desde, LocalDateTime hasta);

    Optional<Visita> findByLeadIdAndEstadoVisita(UUID leadId, EstadoVisita estado);

    boolean existsByLeadIdAndEstadoVisita(UUID leadId, EstadoVisita estado);
}
