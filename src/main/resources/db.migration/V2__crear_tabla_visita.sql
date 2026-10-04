CREATE TABLE visita (
                        id              UUID         PRIMARY KEY,
                        propiedad_id    UUID         NOT NULL,
                        cliente_id      UUID         NOT NULL,
                        agente_id       UUID         NOT NULL,
                        lead_id         UUID         NOT NULL,
                        fecha_hora      TIMESTAMP    NOT NULL,
                        fecha_hora_fin  TIMESTAMP    NOT NULL,
                        estado_visita   VARCHAR(20)  NOT NULL,
                        created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at      TIMESTAMP,

                        CONSTRAINT chk_estado_visita CHECK (estado_visita IN ('PROGRAMADA', 'REALIZADA', 'CANCELADA')),
                        CONSTRAINT chk_visita_rango  CHECK (fecha_hora_fin > fecha_hora)
);

CREATE INDEX idx_visita_lead_id            ON visita (lead_id);
CREATE INDEX idx_visita_cliente_id         ON visita (cliente_id);
CREATE INDEX idx_visita_propiedad_fecha    ON visita (propiedad_id, fecha_hora);
CREATE INDEX idx_visita_agente_fecha       ON visita (agente_id, fecha_hora);