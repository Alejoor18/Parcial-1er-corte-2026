package infrastructure.adapters.out.persistence;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de persistencia para Gafa (R2DBC).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("gafas")
public class GafaEntity {

    @Id
    private UUID id;

    @Column("marca_id")
    private UUID marcaId;

    private String modelo;
}
