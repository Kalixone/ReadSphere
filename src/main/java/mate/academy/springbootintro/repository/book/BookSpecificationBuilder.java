package mate.academy.springbootintro.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.SpecificationBuilder;
import mate.academy.springbootintro.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);

        for (BookSearchParam param : BookSearchParam.values()) {
            String[] values = searchParameters.getParamValues(param);
            if (values != null && values.length > 0) {
                spec = spec.and(specificationProviderManager
                        .getSpecificationProvider(param.name()).getSpecification(values));
            }
        }
        return spec;
    }
}
