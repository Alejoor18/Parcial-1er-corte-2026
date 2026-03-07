package domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Entidad de dominio que representa una Marca.
 * Relación lógica: una Marca puede tener muchas Gafas, pero no se mantiene
 * una colección en el modelo para evitar acoplamiento; las Gafas referencian
 * a su Marca mediante el campo marcaId.
 */
@Value
@Builder
@With
public class Marca {
    UUID id;
    String nombre;
}
