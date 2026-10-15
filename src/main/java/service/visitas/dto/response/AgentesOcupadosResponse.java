package service.visitas.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AgentesOcupadosResponse(

        List<UUID> agentesIds
) {
}
