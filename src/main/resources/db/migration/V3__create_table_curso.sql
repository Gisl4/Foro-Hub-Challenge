CREATE TABLE curso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(100)
);

INSERT INTO curso (nombre, categoria)
VALUES ('Spring Boot', 'Programación');