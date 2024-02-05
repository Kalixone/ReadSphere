package mate.academy.springbootintro.service;

import java.util.List;
import mate.academy.springbootintro.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
