-- Necesaria para usar "=" sobre UUID dentro de un índice GiST (constraints de exclusión)
CREATE EXTENSION IF NOT EXISTS btree_gist;

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
    CONSTRAINT chk_visita_rango  CHECK (fecha_hora_fin > fecha_hora),

    -- Un agente no puede tener dos visitas cuyos rangos se crucen.
    -- Rango [inicio, fin): una visita que termina a las 3:00 no choca con otra que empieza a las 3:00.
    CONSTRAINT ex_visita_agente_sin_solapamiento
        EXCLUDE USING gist (
            agente_id WITH =,
            tsrange(fecha_hora, fecha_hora_fin, '[)') WITH &&
        ) WHERE (estado_visita <> 'CANCELADA'),

    -- Lo mismo para la propiedad: no se puede visitar dos veces a la vez.
    CONSTRAINT ex_visita_propiedad_sin_solapamiento
        EXCLUDE USING gist (
            propiedad_id WITH =,
            tsrange(fecha_hora, fecha_hora_fin, '[)') WITH &&
        ) WHERE (estado_visita <> 'CANCELADA')
);

CREATE INDEX idx_visita_lead_id         ON visita (lead_id);
CREATE INDEX idx_visita_cliente_id      ON visita (cliente_id);
CREATE INDEX idx_visita_propiedad_fecha ON visita (propiedad_id, fecha_hora);
CREATE INDEX idx_visita_agente_fecha    ON visita (agente_id, fecha_hora);

-- Un lead puede tener varias visitas a lo largo del tiempo,
-- pero solo una pendiente (PROGRAMADA) a la vez.
CREATE UNIQUE INDEX uk_visita_lead_programada
    ON visita (lead_id)
    WHERE estado_visita = 'PROGRAMADA';
