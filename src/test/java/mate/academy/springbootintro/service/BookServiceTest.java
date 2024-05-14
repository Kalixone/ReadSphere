package mate.academy.springbootintro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.dto.BookDtoWithoutCategoryIds;
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
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private static final Long CATEGORY_ID_1 = 1L;
    private static final Long CATEGORY_ID_2 = 2L;
    private static final Long BOOK_ID = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String CATEGORY_NAME_1 = "fantasy";
    private static final String CATEGORY_NAME_2 = "action";
    private static final String CATEGORY_DESCRIPTION_1 = "dragons";
    private static final String CATEGORY_DESCRIPTION_2 = "pif_paf";
    private static final String BOOK_TITLE = "Witch";
    private static final String BOOK_AUTHOR = "Wojciech Rogusz";
    private static final String BOOK_COVER_IMAGE = "https://randomImage1.jpg";
    private static final String BOOK_DESCRIPTION = "solid book";
    private static final String BOOK_ISBN = "123-123-123";
    private static final BigDecimal BOOK_PRICE = BigDecimal.TEN;
    private static final String UPDATED_BOOK_TITLE = "Updated Title";
    private static final String UPDATED_BOOK_AUTHOR = "Updated Author";
    private static final BigDecimal UPDATED_BOOK_PRICE = BigDecimal.ONE;
    private static final String UPDATED_BOOK_COVER_IMAGE = "https://Updated_Cover_Image.jpg";
    private static final String UPDATED_BOOK_DESCRIPTION = "Updated Description";

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
        Category category1 = createCategory(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        Category category2 = createCategory(
                CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                BOOK_TITLE, BOOK_AUTHOR,
                BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, Set.of(category1, category2)
        );

        Book book = createBook(
                BOOK_ID, requestDto.title(), requestDto.author(),
                null, requestDto.price(), requestDto.coverImage(),
                requestDto.description(), requestDto.categories()
        );

        BookDto bookDto = createBookDto(
                BOOK_ID, requestDto.title(), requestDto.author(),
                null, requestDto.price(), requestDto.coverImage(),
                requestDto.description(), requestDto.categories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList())
        );

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
        Category category1 = createCategory(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        Category category2 = createCategory(
                CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);

        Book book = createBook(
                BOOK_ID, BOOK_TITLE,
                BOOK_AUTHOR, BOOK_ISBN,
                BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, Set.of(category1, category2));

        BookDto bookDto = createBookDto(
                BOOK_ID, BOOK_TITLE,
                BOOK_AUTHOR, BOOK_ISBN,
                BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, book.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList())
        );

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
        Category category1 = createCategory(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        Category category2 = createCategory(
                CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);

        Book book = createBook(
                BOOK_ID, BOOK_TITLE,
                BOOK_AUTHOR, BOOK_ISBN,
                BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, Set.of(category1, category2));

        BookDto expectedBookDto = createBookDto(
                BOOK_ID, BOOK_TITLE, BOOK_AUTHOR,
                BOOK_ISBN, BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, List.of(CATEGORY_ID_1, CATEGORY_ID_2)
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
                UPDATED_BOOK_TITLE, UPDATED_BOOK_AUTHOR,
                UPDATED_BOOK_PRICE, UPDATED_BOOK_COVER_IMAGE,
                UPDATED_BOOK_DESCRIPTION
        );

        Book existingBook = createBook(
                BOOK_ID, BOOK_TITLE, BOOK_AUTHOR,
                null, BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, null);

        Book updatedBook = createBook(
                BOOK_ID, UPDATED_BOOK_TITLE, UPDATED_BOOK_AUTHOR,
                null, UPDATED_BOOK_PRICE, UPDATED_BOOK_COVER_IMAGE,
                UPDATED_BOOK_DESCRIPTION, null);

        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);

        BookDto expectedBookDto = createBookDto(
                BOOK_ID, UPDATED_BOOK_TITLE,
                UPDATED_BOOK_AUTHOR, null, UPDATED_BOOK_PRICE,
                UPDATED_BOOK_COVER_IMAGE, UPDATED_BOOK_DESCRIPTION,
                List.of());

        when(bookMapper.toDto(updatedBook)).thenReturn(expectedBookDto);

        // When
        BookDto result = bookService.updateBook(BOOK_ID, updateBookRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(BOOK_ID, result.getId());
        assertEquals(UPDATED_BOOK_TITLE, result.getTitle());
        assertEquals(UPDATED_BOOK_AUTHOR, result.getAuthor());
        assertEquals(UPDATED_BOOK_PRICE, result.getPrice());
        assertEquals(UPDATED_BOOK_COVER_IMAGE, result.getCoverImage());
        assertEquals(UPDATED_BOOK_DESCRIPTION, result.getDescription());

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
        Category category1 = createCategory(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        Book book = createBook(
                BOOK_ID, BOOK_TITLE,
                BOOK_AUTHOR, BOOK_ISBN,
                BOOK_PRICE, BOOK_COVER_IMAGE,
                BOOK_DESCRIPTION, Set.of(category1));

        // When
        when(bookRepository.findAllByCategoryId(CATEGORY_ID_1)).thenReturn(List.of(book));
        List<BookDtoWithoutCategoryIds> bookDtos = bookService.findBooksByCategoryId(CATEGORY_ID_1);

        // Then
        assertNotNull(bookDtos);
        assertEquals(1, bookDtos.size());

        BookDtoWithoutCategoryIds expectedBookDto = bookMapper.toDtoWithoutCategories(book);

        assertEquals(expectedBookDto, bookDtos.get(0));
    }

    private Category createCategory(
            Long id, String name,
            String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private Book createBook(
            Long id, String title,
            String author, String isbn,
            BigDecimal price, String coverImage,
            String description, Set<Category> categories) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setCoverImage(coverImage);
        book.setDescription(description);
        book.setCategories(categories);
        return book;
    }

    private BookDto createBookDto(
            Long id, String title,
            String author, String isbn,
            BigDecimal price, String coverImage,
            String description, List<Long> categoriesIds) {
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setPrice(price);
        bookDto.setCoverImage(coverImage);
        bookDto.setDescription(description);
        bookDto.setCategoriesIds
                (categoriesIds != null ? new ArrayList<>(categoriesIds) : Collections.emptyList());
        return bookDto;
    }
}
