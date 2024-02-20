package mate.academy.springbootintro.repository.book;

public record BookSearchParameters(String[] title, String[] author, String[] price) {
}
