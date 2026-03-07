package infrastructure.adapters.out.persistence;

import domain.model.Marca;
import domain.ports.out.MarcaRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del puerto de salida de Marca usando Spring Data R2DBC.
 */
@Component
@RequiredArgsConstructor
public class MarcaRepositoryAdapter implements MarcaRepositoryPort {

    private final MarcaR2dbcRepository repository;

    @Override
    public Mono<Marca> save(Marca marca) {
        return repository.save(toEntity(marca))
                .map(MarcaRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Marca> findById(UUID id) {
        return repository.findById(id).map(MarcaRepositoryAdapter::toDomain);
    }

    @Override
    public Flux<Marca> findAll() {
        return repository.findAll().map(MarcaRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    private static MarcaEntity toEntity(Marca marca) {
        if (marca == null) return null;
        return MarcaEntity.builder()
                .id(marca.getId())
                .nombre(marca.getNombre())
                .build();
    }

    private static Marca toDomain(MarcaEntity entity) {
        if (entity == null) return null;
        return Marca.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }
}
