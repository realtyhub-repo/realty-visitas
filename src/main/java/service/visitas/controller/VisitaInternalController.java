package service.visitas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.visitas.dto.event.MotivoCancelacion;
import service.visitas.dto.request.ReservarVisitaRequest;
import service.visitas.dto.response.AgentesOcupadosResponse;
import service.visitas.dto.response.VisitaResponse;
import service.visitas.service.VisitaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints de servicio a servicio (los usa Lead).
 * No deben exponerse por el Gateway.
 */
@RestController
@RequestMapping("/internal/visitas")
@RequiredArgsConstructor
public class VisitaInternalController {

    private final VisitaService visitaService;

    @GetMapping("/agentes-ocupados")
    public ResponseEntity<AgentesOcupadosResponse> agentesOcupados(
            @RequestParam List<UUID> agenteIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora) {

        return ResponseEntity.ok(visitaService.consultarAgentesOcupados(agenteIds, fechaHora));
    }

    @PostMapping("/reservar")
    public ResponseEntity<VisitaResponse> reservar(@Valid @RequestBody ReservarVisitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaService.reservar(request));
    }

    @DeleteMapping("/lead/{leadId}")
    public ResponseEntity<Void> cancelarPorLead(@PathVariable UUID leadId) {
        visitaService.cancelarPorLead(leadId, MotivoCancelacion.COMPENSACION);
        return ResponseEntity.noContent().build();
    }
}
