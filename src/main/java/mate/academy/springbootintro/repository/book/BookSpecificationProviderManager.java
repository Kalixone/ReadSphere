package mate.academy.springbootintro.repository.book;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.SpecificationProvider;
import mate.academy.springbootintro.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProvider;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProvider.stream()
                .filter(b -> b.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException
                        ("Can't find correct specification provider for key: " + key));
    }
}
