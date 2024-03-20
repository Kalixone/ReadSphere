package mate.academy.springbootintro.dto;

import java.math.BigDecimal;

public record BookDtoWithoutCategoryIds(
        Long id,
        String title,
        String author,
        BigDecimal price,
        String coverImage,
        String description
) {
}
