package com.aluraCursos.ForoHub.repository;

import com.aluraCursos.ForoHub.domain.curso.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}