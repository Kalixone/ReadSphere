package mate.academy.springbootintro.repository.book.spec;

import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.SpecificationProvider;
import mate.academy.springbootintro.repository.book.BookSearchParam;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return BookSearchParam.TITLE.getValue();
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(BookSearchParam.TITLE.getValue()).in(Arrays.stream(params).toArray());
    }
}
