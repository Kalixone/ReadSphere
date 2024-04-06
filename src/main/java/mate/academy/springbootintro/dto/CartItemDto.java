package mate.academy.springbootintro.dto;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity
) {
}
