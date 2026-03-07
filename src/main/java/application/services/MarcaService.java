package application.services;

import domain.model.Marca;
import domain.ports.in.MarcaUseCase;
import domain.ports.out.MarcaRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio de aplicación para operaciones CRUD de Marca.
 */
@Service
@RequiredArgsConstructor
public class MarcaService implements MarcaUseCase {

    private final MarcaRepositoryPort marcaRepository;

    @Override
    public Mono<Marca> crearMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Override
    public Mono<Marca> actualizarMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Override
    public Mono<Void> eliminarMarca(UUID id) {
        return marcaRepository.deleteById(id);
    }

    @Override
    public Mono<Marca> obtenerMarcaPorId(UUID id) {
        return marcaRepository.findById(id);
    }

    @Override
    public Flux<Marca> listarMarcas() {
        return marcaRepository.findAll();
    }
}
