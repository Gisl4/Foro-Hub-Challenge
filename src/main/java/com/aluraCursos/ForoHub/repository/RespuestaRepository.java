package com.aluraCursos.ForoHub.repository;

import com.aluraCursos.ForoHub.domain.respuestas.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    List<Respuesta> findByTopicoId(Long id);
}

