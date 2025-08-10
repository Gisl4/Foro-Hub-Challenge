CREATE TABLE IF NOT EXISTS respuestas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  mensaje TEXT NOT NULL,
  fecha_creacion DATETIME NOT NULL,
  topico_id BIGINT NOT NULL,
  autor_id BIGINT NOT NULL,

  FOREIGN KEY (topico_id) REFERENCES topicos(id),
  FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);
