package infrastructure.mappers;

import domain.model.Marca;
import infrastructure.dto.MarcaDto;

/**
 * Conversión entre modelos de dominio y DTOs de Marca.
 */
public final class MarcaMapper {

    private MarcaMapper() {
        // Utility class
    }

    public static MarcaDto toDto(Marca marca) {
        if (marca == null) return null;
        return new MarcaDto(marca.getId(), marca.getNombre());
    }

    public static Marca toDomain(MarcaDto dto) {
        if (dto == null) return null;
        return Marca.builder()
                .id(dto.id())
                .nombre(dto.nombre())
                .build();
    }
}
