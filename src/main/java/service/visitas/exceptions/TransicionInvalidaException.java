package service.visitas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TransicionInvalidaException extends RuntimeException {
    public TransicionInvalidaException(String message) {
        super(message);
    }
}
