package mate.academy.springbootintro.service;

import mate.academy.springbootintro.dto.BookDtoWithoutCategoryIds;
import org.springframework.data.domain.Pageable;
import java.util.List;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.dto.UpdateBookRequestDto;
import mate.academy.springbootintro.repository.book.BookSearchParameters;

public interface BookService {
    BookDto createBook(CreateBookRequestDto createBookRequestDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getBookById(Long id);

    void deleteById(Long id);

    BookDto updateBook(Long id, UpdateBookRequestDto updateBookRequestDto);

    List<BookDto> search(BookSearchParameters params);

    List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id);
}
