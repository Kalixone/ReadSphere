package mate.academy.springbootintro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.BookDtoWithoutCategoryIds;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.dto.UpdateBookRequestDto;
import mate.academy.springbootintro.mapper.BookMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private static final Long CATEGORY_ID_1 = 1L;
    private static final Long CATEGORY_ID_2 = 2L;
    private static final Long BOOK_ID = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("""
            Verify createBook() method works
            """)
    public void createBook_ValidRequestDto_ReturnsBookDto() {
        // Given
        Category category1 = new Category();
        category1.setId(CATEGORY_ID_1);
        category1.setDescription("fantasy");
        category1.setDescription("dragons");

        Category category2 = new Category();
        category2.setId(CATEGORY_ID_2);
        category2.setName("action");
        category1.setDescription("pif-paf");

        CreateBookRequestDto requestDto = new CreateBookRequestDto
                ("Czarownica",
                        "Ryszard Rogusz",
                        BigDecimal.TEN,
                        "random_cover1.jpg",
                        "great book",
                        Set.of(category1, category2)
                );

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setPrice(requestDto.price());
        book.setCoverImage(requestDto.coverImage());
        book.setDescription(requestDto.description());
        book.setCategories(requestDto.categories());

        BookDto bookDto = new BookDto();
        bookDto.setId(BOOK_ID);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPrice(book.getPrice());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setDescription(book.getDescription());
        bookDto.setCategoriesIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList()));

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto savedBookDto = bookService.createBook(requestDto);

        // Then
        assertThat(savedBookDto).isEqualTo(bookDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("""
            Verify getAll() method works
            """)
    public void getAll_ValidPageable_ReturnsAllBooks() {
        // Given
        Category category1 = new Category();
        category1.setId(CATEGORY_ID_1);
        category1.setDescription("fantasy");
        category1.setDescription("dragons");

        Category category2 = new Category();
        category2.setId(CATEGORY_ID_2);
        category2.setName("action");
        category1.setDescription("pif-paf");

        Book book = new Book();
        book.setId(BOOK_ID);
        book.setTitle("Godzilla");
        book.setAuthor("Vermud");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.TEN);
        book.setCoverImage("random_cover2.jpg");
        book.setDescription("big monster");
        book.setCategories(Set.of(category1, category2));

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setDescription(book.getDescription());
        bookDto.setCategoriesIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toList()));

        Pageable pageable = PageRequest.of(PAGE_NUMBER,PAGE_SIZE);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        List<BookDto> bookDtos = bookService.getAll(pageable);

        // Then
        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.get(0)).isEqualTo(bookDto);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify getById() method works
            """)
    public void getBookById_ValidId_ReturnsBookDto() {
        // Given
        Category category1 = new Category();
        category1.setId(CATEGORY_ID_1);
        category1.setDescription("fantasy");
        category1.setDescription("dragons");

        Category category2 = new Category();
        category2.setId(CATEGORY_ID_2);
        category2.setName("action");
        category1.setDescription("pif-paf");

        Book book = new Book();
        book.setId(BOOK_ID);
        book.setTitle("Godzilla");
        book.setAuthor("Vermud");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.TEN);
        book.setCoverImage("random_cover2.jpg");
        book.setDescription("big monster");
        book.setCategories(Set.of(category1, category2));

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(book.getId());
        expectedBookDto.setTitle(book.getTitle());
        expectedBookDto.setAuthor(book.getAuthor());
        expectedBookDto.setIsbn(book.getIsbn());
        expectedBookDto.setPrice(book.getPrice());
        expectedBookDto.setCoverImage(book.getCoverImage());
        expectedBookDto.setDescription(book.getDescription());
        expectedBookDto.setCategoriesIds(
                book.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList())
        );

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        // When
        BookDto actualBookDto = bookService.getBookById(BOOK_ID);

        // Then
        assertThat(actualBookDto).isEqualTo(expectedBookDto);

        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify deleteById() method works
            """)
    public void deleteById_ValidId_DeletesBook() {
        // When
        bookService.deleteById(BOOK_ID);

        // Then
        verify(bookRepository, times(1)).deleteById(BOOK_ID);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify updateBook() method works
            """)
    public void updateBook_ValidBookRequestDto_ReturnsBookDto() {
        // Given
        UpdateBookRequestDto updateBookRequestDto = new UpdateBookRequestDto(
                "Updated Title",
                "Updated Author",
                new BigDecimal("19.99"),
                "updated_cover_image.jpg",
                "Updated Description"
        );
        Book existingBook = new Book();
        existingBook.setId(BOOK_ID);
        existingBook.setTitle("Original Title");
        existingBook.setAuthor("Original Author");
        existingBook.setPrice(new BigDecimal("10.99"));
        existingBook.setCoverImage("original_cover_image.jpg");
        existingBook.setDescription("Original Description");

        Book updatedBook = new Book();
        updatedBook.setId(BOOK_ID);
        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");
        updatedBook.setPrice(new BigDecimal("19.99"));
        updatedBook.setCoverImage("updated_cover_image.jpg");
        updatedBook.setDescription("Updated Description");

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);

        BookDto expectedBookDto = new BookDto();
        expectedBookDto.setId(BOOK_ID);
        expectedBookDto.setTitle("Updated Title");
        expectedBookDto.setAuthor("Updated Author");
        expectedBookDto.setPrice(new BigDecimal("19.99"));
        expectedBookDto.setCoverImage("updated_cover_image.jpg");
        expectedBookDto.setDescription("Updated Description");
        expectedBookDto.setCategoriesIds(List.of());

        when(bookMapper.toDto(updatedBook)).thenReturn(expectedBookDto);

        // When
        BookDto result = bookService.updateBook(BOOK_ID, updateBookRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(BOOK_ID, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        assertEquals(new BigDecimal("19.99"), result.getPrice());
        assertEquals("updated_cover_image.jpg", result.getCoverImage());
        assertEquals("Updated Description", result.getDescription());

        verify(bookRepository, times(1)).findById(BOOK_ID);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper, times(1)).toDto(updatedBook);
    }

    @Test
    @DisplayName("""
            Verify findBooksByCategoryId() method works
            """)
    public void findBooksByCategoryId_ValidId_ReturnsBookDtoWithoutCategoryIds() {
        // Given
        Category category1 = new Category();
        category1.setId(CATEGORY_ID_1);
        category1.setName("Fantasy");
        category1.setDescription("Dragons and magic");

        Book book = new Book();
        book.setTitle("Godzilla");
        book.setAuthor("Vermud");
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.TEN);
        book.setCoverImage("random_cover2.jpg");
        book.setDescription("big monster");
        book.setCategories(Set.of(category1));

        // When
        when(bookRepository.findAllByCategoryId(CATEGORY_ID_1)).thenReturn(List.of(book));
        List<BookDtoWithoutCategoryIds> bookDtos = bookService.findBooksByCategoryId(CATEGORY_ID_1);

        // Then
        assertNotNull(bookDtos);
        assertEquals(1, bookDtos.size());

        BookDtoWithoutCategoryIds expectedBookDto = bookMapper.toDtoWithoutCategories(book);

        assertEquals(expectedBookDto, bookDtos.get(0));
    }
}
