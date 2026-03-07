package application.services;

import domain.model.Gafa;
import domain.ports.in.GafaUseCase;
import domain.ports.out.GafaRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio de aplicación para operaciones CRUD de Gafa.
 */
@Service
@RequiredArgsConstructor
public class GafaService implements GafaUseCase {

    private final GafaRepositoryPort gafaRepository;

    @Override
    public Mono<Gafa> crearGafa(Gafa gafa) {
        return gafaRepository.save(gafa);
    }

    @Override
    public Mono<Gafa> actualizarGafa(Gafa gafa) {
        return gafaRepository.save(gafa);
    }

    @Override
    public Mono<Void> eliminarGafa(UUID id) {
        return gafaRepository.deleteById(id);
    }

    @Override
    public Mono<Gafa> obtenerGafaPorId(UUID id) {
        return gafaRepository.findById(id);
    }

    @Override
    public Flux<Gafa> listarGafas() {
        return gafaRepository.findAll();
    }

    @Override
    public Flux<Gafa> listarGafasPorMarca(UUID marcaId) {
        return gafaRepository.findByMarcaId(marcaId);
    }
}
