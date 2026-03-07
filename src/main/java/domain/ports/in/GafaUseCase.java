package domain.ports.in;

import domain.model.Gafa;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Casos de uso relacionados con Gafas.
 */
public interface GafaUseCase {

    Mono<Gafa> crearGafa(Gafa gafa);

    Mono<Gafa> actualizarGafa(Gafa gafa);

    Mono<Void> eliminarGafa(UUID id);

    Mono<Gafa> obtenerGafaPorId(UUID id);

    Flux<Gafa> listarGafas();

    Flux<Gafa> listarGafasPorMarca(UUID marcaId);
}
