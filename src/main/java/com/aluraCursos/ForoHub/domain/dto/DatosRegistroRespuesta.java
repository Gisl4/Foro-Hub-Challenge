package com.aluraCursos.ForoHub.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroRespuesta(
        @NotNull Long topicoId,
        @NotBlank String mensaje
) {}
