package com.aluraCursos.ForoHub.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record DatosActualizacionTopico(
        @NotBlank String titulo,
        @NotBlank String mensaje,
        @NotBlank String status
) {}
