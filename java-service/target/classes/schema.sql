CREATE TABLE IF NOT EXISTS dispositivos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  zona VARCHAR(10) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  activo BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS lecturas (
  id SERIAL PRIMARY KEY,
  dispositivo_id INTEGER NOT NULL REFERENCES dispositivos(id) ON DELETE CASCADE,
  valor DOUBLE PRECISION NOT NULL,
  fecha TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS umbrales (
  id SERIAL PRIMARY KEY,
  dispositivo_id INTEGER NOT NULL REFERENCES dispositivos(id) ON DELETE CASCADE,
  minimo DOUBLE PRECISION,
  maximo DOUBLE PRECISION
);

-- Opcional: tabla alertas
CREATE TABLE IF NOT EXISTS alertas (
  id SERIAL PRIMARY KEY,
  dispositivo_id INTEGER,
  nivel VARCHAR(50),
  mensaje TEXT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  activa BOOLEAN DEFAULT true
);
