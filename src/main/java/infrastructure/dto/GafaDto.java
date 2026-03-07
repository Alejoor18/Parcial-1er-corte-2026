package infrastructure.dto;

import java.util.UUID;

/**
 * DTO para exponer datos de Gafa a través de adaptadores.
 */
public record GafaDto(
        UUID id,
        UUID marcaId,
        String modelo
) { }
