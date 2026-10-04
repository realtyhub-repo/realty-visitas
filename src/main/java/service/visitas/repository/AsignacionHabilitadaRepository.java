package service.visitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.visitas.entity.AsignacionHabilitada;

import java.util.UUID;

public interface AsignacionHabilitadaRepository extends JpaRepository<AsignacionHabilitada, UUID> {
}
