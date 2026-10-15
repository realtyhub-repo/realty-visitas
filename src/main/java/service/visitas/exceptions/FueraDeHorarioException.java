package service.visitas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FueraDeHorarioException extends RuntimeException {
    public FueraDeHorarioException(String message) {
        super(message);
    }
}
