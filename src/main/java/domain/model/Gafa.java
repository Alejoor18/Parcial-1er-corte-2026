package domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

/**
 * Entidad de dominio que representa una Gafa (par de gafas/lentes).
 * Cada Gafa pertenece a una Marca, referenciada por marcaId.
 */
@Value
@Builder
@With
public class Gafa {
    UUID id;
    UUID marcaId;
    String modelo;
}
