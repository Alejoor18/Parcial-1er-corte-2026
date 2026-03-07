package infrastructure.dto;

import java.util.UUID;

/**
 * DTO para exponer datos de Marca a través de adaptadores.
 */
public record MarcaDto(
        UUID id,
        String nombre
) { }
