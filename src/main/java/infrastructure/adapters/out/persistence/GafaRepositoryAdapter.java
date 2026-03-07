package infrastructure.adapters.out.persistence;

import domain.model.Gafa;
import domain.ports.out.GafaRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del puerto de salida de Gafa usando Spring Data R2DBC.
 */
@Component
@RequiredArgsConstructor
public class GafaRepositoryAdapter implements GafaRepositoryPort {

    private final GafaR2dbcRepository repository;

    @Override
    public Mono<Gafa> save(Gafa gafa) {
        return repository.save(toEntity(gafa))
                .map(GafaRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Gafa> findById(UUID id) {
        return repository.findById(id).map(GafaRepositoryAdapter::toDomain);
    }

    @Override
    public Flux<Gafa> findAll() {
        return repository.findAll().map(GafaRepositoryAdapter::toDomain);
    }

    @Override
    public Flux<Gafa> findByMarcaId(UUID marcaId) {
        return repository.findByMarcaId(marcaId).map(GafaRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    private static GafaEntity toEntity(Gafa gafa) {
        if (gafa == null) return null;
        return GafaEntity.builder()
                .id(gafa.getId())
                .marcaId(gafa.getMarcaId())
                .modelo(gafa.getModelo())
                .build();
    }

    private static Gafa toDomain(GafaEntity entity) {
        if (entity == null) return null;
        return Gafa.builder()
                .id(entity.getId())
                .marcaId(entity.getMarcaId())
                .modelo(entity.getModelo())
                .build();
    }
}
