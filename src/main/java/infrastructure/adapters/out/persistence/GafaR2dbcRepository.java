package infrastructure.adapters.out.persistence;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio R2DBC para Gafa.
 */
@Repository
public interface GafaR2dbcRepository extends org.springframework.data.repository.reactive.ReactiveCrudRepository<GafaEntity, UUID> {

    Flux<GafaEntity> findByMarcaId(UUID marcaId);
}
