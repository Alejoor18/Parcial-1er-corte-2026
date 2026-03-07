package infrastructure.adapters.out.persistence;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad de persistencia para Marca (R2DBC).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("marcas")
public class MarcaEntity {

    @Id
    private UUID id;

    private String nombre;
}
