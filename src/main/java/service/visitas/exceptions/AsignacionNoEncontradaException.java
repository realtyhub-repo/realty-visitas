package service.visitas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AsignacionNoEncontradaException extends RuntimeException {
    public AsignacionNoEncontradaException(String message) {
        super(message);
    }
}
