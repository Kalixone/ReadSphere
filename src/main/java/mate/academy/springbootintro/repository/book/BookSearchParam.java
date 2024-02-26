package mate.academy.springbootintro.repository.book;

import lombok.Getter;

public enum BookSearchParam {
    TITLE("title"),
    AUTHOR("author"),
    PRICE("price");

    @Getter
    private String value;

    BookSearchParam(String value) {
        this.value = value;
    }

    public static BookSearchParam fromValue(String s) {
        for (BookSearchParam p : values()) {
            if (p.getValue().equals(s)) {
                return p;
            }
        }
        throw new IllegalArgumentException("No enum constant for value");
    }
}
