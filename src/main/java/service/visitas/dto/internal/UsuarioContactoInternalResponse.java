package service.visitas.dto.internal;

import java.util.UUID;

public record UsuarioContactoInternalResponse(
        UUID id,
        String nombre,
        String telefono,
        String email
) {
}
