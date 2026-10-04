CREATE TABLE asignacion_habilitada (
                                       lead_id       UUID       PRIMARY KEY,
                                       propiedad_id  UUID       NOT NULL,
                                       cliente_id    UUID       NOT NULL,
                                       agente_id     UUID       NOT NULL,
                                       created_at    TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);