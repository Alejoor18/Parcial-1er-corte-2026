package infrastructure.mappers;

import domain.model.Gafa;
import infrastructure.dto.GafaDto;

/**
 * Conversión entre modelos de dominio y DTOs de Gafa.
 */
public final class GafaMapper {

    private GafaMapper() {
        // Utility class
    }

    public static GafaDto toDto(Gafa gafa) {
        if (gafa == null) return null;
        return new GafaDto(gafa.getId(), gafa.getMarcaId(), gafa.getModelo());
    }

    public static Gafa toDomain(GafaDto dto) {
        if (dto == null) return null;
        return Gafa.builder()
                .id(dto.id())
                .marcaId(dto.marcaId())
                .modelo(dto.modelo())
                .build();
    }
}
