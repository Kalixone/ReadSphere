package mate.academy.springbootintro.repository.book.spec;

import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return "price";
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get("price").in(Arrays.stream(params).toArray());
    }
}
