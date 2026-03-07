package infrastructure.adapters.out.persistence;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio R2DBC para Marca.
 */
@Repository
public interface MarcaR2dbcRepository extends R2dbcRepository<MarcaEntity, UUID> {
}
