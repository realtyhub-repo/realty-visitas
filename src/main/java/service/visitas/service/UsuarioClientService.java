package service.visitas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import service.visitas.dto.internal.UsuarioContactoInternalResponse;

import java.util.UUID;

@Service
@Slf4j
public class UsuarioClientService {

    private final RestClient usuarioClient;

    public UsuarioClientService(@Qualifier("usuarioRestClient") RestClient usuarioClient) {
        this.usuarioClient = usuarioClient;
    }

    public String buscarNombre(UUID usuarioId) {
        try {
            UsuarioContactoInternalResponse contacto = usuarioClient.get()
                    .uri("/internal/usuario/{id}/contacto", usuarioId)
                    .retrieve()
                    .body(UsuarioContactoInternalResponse.class);

            return contacto != null ? contacto.nombre() : null;
        } catch (RestClientException e) {
            log.warn("No se pudo obtener el nombre del usuario {}: {}", usuarioId, e.getMessage());
            return null;
        }
    }
}
