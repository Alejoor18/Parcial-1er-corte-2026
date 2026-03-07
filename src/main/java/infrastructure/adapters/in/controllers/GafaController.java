package infrastructure.adapters.in.controllers;

import domain.ports.in.GafaUseCase;
import infrastructure.dto.GafaDto;
import infrastructure.mappers.GafaMapper;
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
 * Controlador reactivo para operaciones CRUD de Gafa.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/gafas", produces = MediaType.APPLICATION_JSON_VALUE)
public class GafaController {

    private final GafaUseCase gafaUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GafaDto> crear(@RequestBody Mono<GafaDto> request) {
        return request
                .map(GafaMapper::toDomain)
                .flatMap(gafaUseCase::crearGafa)
                .map(GafaMapper::toDto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GafaDto> actualizar(@PathVariable UUID id, @RequestBody Mono<GafaDto> request) {
        return request
                .map(GafaMapper::toDomain)
                .map(g -> g.withId(id))
                .flatMap(gafaUseCase::actualizarGafa)
                .map(GafaMapper::toDto);
    }

    @GetMapping("/{id}")
    public Mono<GafaDto> obtenerPorId(@PathVariable UUID id) {
        return gafaUseCase.obtenerGafaPorId(id).map(GafaMapper::toDto);
    }

    @GetMapping
    public Flux<GafaDto> listar() {
        return gafaUseCase.listarGafas().map(GafaMapper::toDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> eliminar(@PathVariable UUID id) {
        return gafaUseCase.eliminarGafa(id);
    }

    /**
     * Lista las gafas pertenecientes a una marca específica.
     */
    @GetMapping(path = "/api/v1/marcas/{marcaId}/gafas", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<GafaDto> listarPorMarca(@PathVariable UUID marcaId) {
        return gafaUseCase.listarGafasPorMarca(marcaId).map(GafaMapper::toDto);
    }
}
