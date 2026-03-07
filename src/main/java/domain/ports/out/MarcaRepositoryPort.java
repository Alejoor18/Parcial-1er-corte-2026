package domain.ports.out;

import domain.model.Marca;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para operaciones de persistencia de Marca.
 */
public interface MarcaRepositoryPort {

    Mono<Marca> save(Marca marca);

    Mono<Marca> findById(UUID id);

    Flux<Marca> findAll();

    Mono<Void> deleteById(UUID id);
}
