package mate.academy.springbootintro.dto;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity
) {
}
