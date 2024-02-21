package mate.academy.springbootintro.dto;

import java.math.BigDecimal;

public record CreateBookRequestDto(
        String title,
        String author,
        BigDecimal price,
        String coverImage,
        String description
) {
}
