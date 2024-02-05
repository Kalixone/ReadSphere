package mate.academy.springbootintro.repository;

import mate.academy.springbootintro.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
