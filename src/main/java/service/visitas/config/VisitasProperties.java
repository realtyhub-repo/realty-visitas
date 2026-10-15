package service.visitas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalTime;

@ConfigurationProperties(prefix = "visitas")
public record VisitasProperties(
        int duracionMinutos,
        LocalTime horaApertura,
        LocalTime horaCierre
) {}