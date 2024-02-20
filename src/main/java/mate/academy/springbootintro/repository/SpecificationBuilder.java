package mate.academy.springbootintro.repository;

import mate.academy.springbootintro.repository.book.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters searchParameters);
}
