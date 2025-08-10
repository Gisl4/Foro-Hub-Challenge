package com.aluraCursos.ForoHub.controller;


import com.aluraCursos.ForoHub.domain.dto.DatosRegistroRespuesta;
import com.aluraCursos.ForoHub.domain.respuestas.Respuesta;
import com.aluraCursos.ForoHub.domain.usuario.Usuario;
import com.aluraCursos.ForoHub.repository.RespuestaRepository;
import com.aluraCursos.ForoHub.repository.TopicoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> responder(@RequestBody @Valid DatosRegistroRespuesta datos,
                                       @AuthenticationPrincipal Usuario usuario) {

        var topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado"));

        var respuesta = new Respuesta();
        respuesta.setMensaje(datos.mensaje());
        respuesta.setTopico(topico);
        respuesta.setAutor(usuario);

        respuestaRepository.save(respuesta);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Respuesta registrada correctamente");
    }
}