package mate.academy.springbootintro.repository.book;

public record BookSearchParameters(String[] title, String[] author, String[] price) {

    public String[] getParamValues(BookSearchParam param) {
        switch (param) {
            case TITLE:
                return title;
            case AUTHOR:
                return author;
            case PRICE:
                return price;
            default:
                throw new IllegalArgumentException("Valid search parameter: " + param);
        }
    }
}
