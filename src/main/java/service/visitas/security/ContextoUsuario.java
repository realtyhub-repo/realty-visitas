package service.visitas.security;


import service.visitas.dto.internal.RolUsuario;

import java.util.UUID;

public record ContextoUsuario(UUID userId, RolUsuario rol) {}