CREATE TABLE topicos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(255) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha_creacion DATETIME NOT NULL,
  status VARCHAR(50),
  autor BIGINT,
  curso BIGINT
);

