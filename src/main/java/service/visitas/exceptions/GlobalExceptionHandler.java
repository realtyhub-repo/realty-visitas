package service.visitas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import service.visitas.dto.internal.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContextoUsuarioInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleContextoUsuarioInvalido(ContextoUsuarioInvalidoException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "CONTEXTO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public ResponseEntity<ErrorResponse> handleAccesoNoAutorizado(AccesoNoAutorizadoException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, "ACCESO_NO_AUTORIZADO", ex.getMessage());
    }

    @ExceptionHandler(VisitaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleVisitaNoEncontrada(VisitaNoEncontradaException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, "VISITA_NO_ENCONTRADA", ex.getMessage());
    }

    @ExceptionHandler(FechaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleFechaInvalida(FechaInvalidaException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "FECHA_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(FueraDeHorarioException.class)
    public ResponseEntity<ErrorResponse> handleFueraDeHorario(FueraDeHorarioException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "FUERA_DE_HORARIO", ex.getMessage());
    }

    @ExceptionHandler(AgenteOcupadoException.class)
    public ResponseEntity<ErrorResponse> handleAgenteOcupado(AgenteOcupadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, "AGENTE_OCUPADO", ex.getMessage());
    }

    @ExceptionHandler(PropiedadOcupadaException.class)
    public ResponseEntity<ErrorResponse> handlePropiedadOcupada(PropiedadOcupadaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, "PROPIEDAD_OCUPADA", ex.getMessage());
    }

    @ExceptionHandler(VisitaYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleVisitaYaExiste(VisitaYaExisteException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, "VISITA_YA_EXISTE", ex.getMessage());
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionInvalida(TransicionInvalidaException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, "TRANSICION_INVALIDA", ex.getMessage());
    }

    // --- Errores de formato de la petición ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Solicitud inválida");
        return construirRespuesta(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", mensaje);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleSolicitudMalFormada(Exception ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA",
                "La solicitud tiene parámetros faltantes o con formato inválido");
    }

    private ResponseEntity<ErrorResponse> construirRespuesta(HttpStatus status, String codigo, String mensaje) {
        ErrorResponse error = new ErrorResponse(codigo, mensaje, status.value(), LocalDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
