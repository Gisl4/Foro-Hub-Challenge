package com.aluraCursos.ForoHub.controller;

import com.aluraCursos.ForoHub.domain.dto.*;
import com.aluraCursos.ForoHub.domain.topico.Topico;
import com.aluraCursos.ForoHub.domain.usuario.Usuario;
import com.aluraCursos.ForoHub.domain.curso.Curso;
import com.aluraCursos.ForoHub.repository.RespuestaRepository;
import com.aluraCursos.ForoHub.repository.TopicoRepository;
import com.aluraCursos.ForoHub.repository.UsuarioRepository;
import com.aluraCursos.ForoHub.repository.CursoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrar(@RequestBody @Valid DatosRegistroTopico datos) {

        boolean existe = topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje());
        if (existe) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un tópico con ese título y mensaje");
        }

        Usuario autor = usuarioRepository.findById(datos.autor())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));

        Curso curso = cursoRepository.findById(datos.curso())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Topico topico = new Topico();
        topico.setTitulo(datos.titulo());
        topico.setMensaje(datos.mensaje());
        topico.setStatus(datos.status());
        topico.setAutor(autor);
        topico.setCurso(curso);

        topicoRepository.save(topico);

        var dto = new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getStatus(),
                autor.getUsername(),
                curso.getNombre()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("Tópico registrado correctamente");
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listar(
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion) {

        var listado = topicoRepository.findAll(paginacion)
                .map(t -> new DatosListadoTopico(
                        t.getId(),
                        t.getTitulo(),
                        t.getMensaje(),
                        t.getFechaCreacion(),
                        t.getStatus(),
                        t.getAutor().getUsername(),
                        t.getCurso().getNombre()
                ));

        return ResponseEntity.ok(listado);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<DatosListadoTopico>> filtrarPorCursoYAño(
            @RequestParam String curso,
            @RequestParam int año) {

        LocalDateTime inicio = LocalDateTime.of(año, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(año + 1, 1, 1, 0, 0);

        List<Topico> topicos = topicoRepository
                .findByCurso_NombreAndFechaCreacionBetween(curso, inicio, fin);

        var dtoList = topicos.stream()
                .map(t -> new DatosListadoTopico(
                        t.getId(), t.getTitulo(), t.getMensaje(), t.getFechaCreacion(),
                        t.getStatus(), t.getAutor().getUsername(), t.getCurso().getNombre()))
                .toList();

        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> detalleTopico(@PathVariable Long id) {
        var topico = topicoRepository.findById(id)
                .orElse(null);

        if (topico == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tópico no encontrado");
        }

        var dto = new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getStatus(),
                topico.getAutor().getUsername(),
                topico.getCurso().getNombre()
        );

        return ResponseEntity.ok(dto);
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody @Valid DatosActualizacionTopico datos) {

        var topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tópico no encontrado");
        }

        var topico = topicoOptional.get();
        topico.setTitulo(datos.titulo());
        topico.setMensaje(datos.mensaje());
        topico.setStatus(datos.status());

        var dto = new DatosDetalleTopico(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getStatus(),
                topico.getAutor().getUsername(),
                topico.getCurso().getNombre()
        );

        return ResponseEntity.ok(dto);
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        var topicoOptional = topicoRepository.findById(id);

        if (topicoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tópico no encontrado");
        }

        topicoRepository.deleteById(id);
        return ResponseEntity.ok("Tópico eliminado correctamente");
    }
    @GetMapping("/{id}/respuestas")
    public ResponseEntity<List<DatosRespuestaListado>> listarRespuestas(@PathVariable Long id) {
        var respuestas = respuestaRepository.findByTopicoId(id)
                .stream()
                .map(respuesta -> new DatosRespuestaListado(
                        respuesta.getId(),
                        respuesta.getMensaje(),
                        respuesta.getAutor().getLogin(),
                        respuesta.getFechaCreacion()
                )).toList();

        return ResponseEntity.ok(respuestas);
    }
}





