package mate.academy.springbootintro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.validation.CoverImage;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @NotNull
        String title,
        @NotNull
        String author,
        @NotNull
        @Min(0)
        BigDecimal price,
        @CoverImage
        String coverImage,
        String description,
        Set<Category> categories
) {
}
