-- Habilita funciones para generar UUID (requerido para valores por defecto)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabla de Marcas
CREATE TABLE IF NOT EXISTS marcas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL
);

-- Tabla de Gafas
CREATE TABLE IF NOT EXISTS gafas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    marca_id UUID NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    CONSTRAINT fk_gafas_marca
        FOREIGN KEY (marca_id) REFERENCES marcas (id) ON DELETE RESTRICT
);

-- Índice para acelerar consultas por marca_id
CREATE INDEX IF NOT EXISTS idx_gafas_marca_id ON gafas (marca_id);
