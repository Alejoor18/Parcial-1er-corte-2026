package domain.ports.out;

import domain.model.Gafa;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para operaciones de persistencia de Gafa.
 */
public interface GafaRepositoryPort {

    Mono<Gafa> save(Gafa gafa);

    Mono<Gafa> findById(UUID id);

    Flux<Gafa> findAll();

    Flux<Gafa> findByMarcaId(UUID marcaId);

    Mono<Void> deleteById(UUID id);
}
