package mate.academy.springbootintro.service;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.exception.EntityNotFoundException;
import mate.academy.springbootintro.mapper.BookMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.repository.BookRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        book.setIsbn("abc" + new Random().nextInt(1000));
        Book savedBook = bookRepository.createBook(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.getAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.getBookById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);

    }
}
