package service.visitas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.visitas.dto.request.CambiarEstadoVisitaRequest;
import service.visitas.dto.response.OcupacionResponse;
import service.visitas.dto.response.VisitaResponse;
import service.visitas.entity.EstadoVisita;
import service.visitas.security.ContextoUsuario;
import service.visitas.security.UsuarioActual;
import service.visitas.service.VisitaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visitas")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;

    // Calendario del front: qué horas ya están tomadas para esa propiedad ese día
    @GetMapping("/propiedad/{propiedadId}/ocupacion")
    public ResponseEntity<List<OcupacionResponse>> obtenerOcupacion(
            @PathVariable UUID propiedadId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(visitaService.obtenerOcupacionPropiedad(propiedadId, fecha));
    }

    @GetMapping("/mis-visitas")
    public ResponseEntity<List<VisitaResponse>> listarMisVisitas(
            @UsuarioActual ContextoUsuario contexto,
            @RequestParam(required = false) EstadoVisita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {

        return ResponseEntity.ok(visitaService.listarMisVisitas(contexto, estado, desde, hasta));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<VisitaResponse> cambiarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarEstadoVisitaRequest request,
            @UsuarioActual ContextoUsuario contexto) {

        return ResponseEntity.ok(visitaService.cambiarEstado(id, request.nuevoEstado(), contexto));
    }
}
