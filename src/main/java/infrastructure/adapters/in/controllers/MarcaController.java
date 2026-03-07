package infrastructure.adapters.in.controllers;

import domain.ports.in.MarcaUseCase;
import infrastructure.dto.MarcaDto;
import infrastructure.mappers.MarcaMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador reactivo para operaciones CRUD de Marca.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/marcas", produces = MediaType.APPLICATION_JSON_VALUE)
public class MarcaController {

    private final MarcaUseCase marcaUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MarcaDto> crear(@RequestBody Mono<MarcaDto> request) {
        return request
                .map(MarcaMapper::toDomain)
                .flatMap(marcaUseCase::crearMarca)
                .map(MarcaMapper::toDto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MarcaDto> actualizar(@PathVariable UUID id, @RequestBody Mono<MarcaDto> request) {
        return request
                .map(MarcaMapper::toDomain)
                .map(m -> m.withId(id))
                .flatMap(marcaUseCase::actualizarMarca)
                .map(MarcaMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<MarcaDto> obtenerPorId(@PathVariable UUID id) {
        return marcaUseCase.obtenerMarcaPorId(id).map(MarcaMapper::toDto);
    }

    @GetMapping
    public Flux<MarcaDto> listar() {
        return marcaUseCase.listarMarcas().map(MarcaMapper::toDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminar(@PathVariable UUID id) {
        return marcaUseCase.eliminarMarca(id);
    }
}
