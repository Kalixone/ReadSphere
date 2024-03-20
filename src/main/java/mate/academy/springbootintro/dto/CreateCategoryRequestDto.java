package mate.academy.springbootintro.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDto(
        @NotBlank
        String name,
        String description
) {
}
