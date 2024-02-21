package mate.academy.springbootintro.dto;

import java.math.BigDecimal;

public record UpdateBookRequestDto(
        String title,
        String author,
        BigDecimal price,
        String coverImage,
        String description
) {
}
