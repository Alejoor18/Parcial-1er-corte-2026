package domain.ports.in;

import domain.model.Marca;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Casos de uso relacionados con Marcas.
 */
public interface MarcaUseCase {

    Mono<Marca> crearMarca(Marca marca);

    Mono<Marca> actualizarMarca(Marca marca);

    Mono<Void> eliminarMarca(UUID id);

    Mono<Marca> obtenerMarcaPorId(UUID id);

    Flux<Marca> listarMarcas();
}
