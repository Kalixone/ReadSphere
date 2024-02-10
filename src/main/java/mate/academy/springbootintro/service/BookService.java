package mate.academy.springbootintro.service;

import java.util.List;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;

public interface BookService {
    BookDto createBook(CreateBookRequestDto createBookRequestDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);
}
