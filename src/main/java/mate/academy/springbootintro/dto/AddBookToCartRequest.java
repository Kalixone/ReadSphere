package mate.academy.springbootintro.dto;

public record AddBookToCartRequest(
        Long bookId,
        int quantity
) {
}
