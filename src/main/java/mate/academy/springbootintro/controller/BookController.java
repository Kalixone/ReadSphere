package mate.academy.springbootintro.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.service.BookService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.getAll();
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto createBookRequestDto) {
        return bookService.createBook(createBookRequestDto);
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }
}
