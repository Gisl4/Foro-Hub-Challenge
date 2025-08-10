package com.aluraCursos.ForoHub.infra.errores;

public class ValidacionException extends RuntimeException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}

