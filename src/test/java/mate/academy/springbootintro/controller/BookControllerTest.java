package mate.academy.springbootintro.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootintro.config.CustomMySqlContainer;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.book.BookRepository;
import mate.academy.springbootintro.repository.category.CategoryRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    protected static MockMvc mockMvc;
    private static final Long BOOK_ID_1 = 1L;
    private static final Long BOOK_ID_2 = 2L;
    private static final Long BOOK_ID_3 = 3L;
    private static final String SPRING_DATASOURCE_URL =
            "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME =
            "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD =
            "spring.datasource.password";
    private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME =
            "spring.datasource.driver-class-name";
    private static final String CATEGORY_NAME = "magic";
    private static final String CATEGORY_DESCRIPTION = "dragons";
    private static final String BOOK_TITLE_1 = "Rekrut";
    private static final String BOOK_AUTHOR_1 = "Vanbord";
    private static final String BOOK_COVER_IMAGE_1 = "https://randomImage1.jpg";
    private static final String BOOK_DESCRIPTION_1 = "great book";
    private static final BigDecimal BOOK_PRICE_1 = BigDecimal.TEN;
    private static final BigDecimal BOOK_PRICE_2 = BigDecimal.valueOf(19.99);
    private static final String BOOK_ISBN_1 = "123456789";
    private static final String BOOK_SEARCH_TITLE = "Harry Potter";
    private static final String BOOK_SEARCH_AUTHOR = "Rowling";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        CustomMySqlContainer mysqlContainer = CustomMySqlContainer.getInstance();
        registry.add(SPRING_DATASOURCE_URL, mysqlContainer::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_USERNAME, mysqlContainer::getUsername);
        registry.add(SPRING_DATASOURCE_PASSWORD, mysqlContainer::getPassword);
        registry.add(SPRING_DATASOURCE_DRIVER_CLASS_NAME, mysqlContainer::getDriverClassName);
    }

    @BeforeAll
    static void beforeAll() {
        CustomMySqlContainer.getInstance().start();
    }

    @AfterAll
    static void afterAll() {
        CustomMySqlContainer.getInstance().stop();
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Create a new book
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books_categories/" +
                    "delete-book_id-and_category_id-from-books_categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/categories/delete-categories-from-categories-table.sql"

    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_CreatesNewBook() throws Exception {
        // Given
        Category category = createCategory(
                CATEGORY_NAME, CATEGORY_DESCRIPTION);

        categoryRepository.save(category);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                BOOK_TITLE_1,
                BOOK_AUTHOR_1,
                BOOK_PRICE_1,
                BOOK_COVER_IMAGE_1,
                BOOK_DESCRIPTION_1,
                Set.of(category)
        );

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.title());
        expected.setAuthor(requestDto.author());
        expected.setPrice(requestDto.price());
        expected.setCoverImage(requestDto.coverImage());
        expected.setDescription(requestDto.description());
        expected.setCategoriesIds(requestDto.categories().stream()
                .map(Category::getId)
                        .collect(Collectors.toList()));

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                post("/api/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper.readValue
                (result.getResponse().getContentAsString(),
                        BookDto.class);

        EqualsBuilder.reflectionEquals(expected, actual, "id");
        Assertions.assertIterableEquals(expected.getCategoriesIds(), actual.getCategoriesIds());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get all books
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_ValidRequest_ReturnsAllBooks() throws Exception {
        // Given
        List<BookDto> expected = prepareExpectedBooks();

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        Assertions.assertIterableEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get book by ID
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_ValidId_ReturnsBookDto() throws Exception {
        // Given
        BookDto bookDto = createBookDto(
                BOOK_ID_1, BOOK_TITLE_1,
                BOOK_AUTHOR_1, BOOK_ISBN_1,
                BOOK_PRICE_1, BOOK_COVER_IMAGE_1,
                BOOK_DESCRIPTION_1, Collections.emptyList());

        BookDto expected = createBookDto(
                bookDto.getId(), bookDto.getTitle(),
                bookDto.getAuthor(), bookDto.getIsbn(),
                bookDto.getPrice(), bookDto.getCoverImage(),
                bookDto.getDescription(), bookDto.getCategoriesIds());

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/books/{id}", BOOK_ID_1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);

        EqualsBuilder.reflectionEquals(expected, actual, "id");
        Assertions.assertIterableEquals(expected.getCategoriesIds(), actual.getCategoriesIds());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Delete book by ID
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookById_ValidId_DeletesBook() throws Exception {
        // Given
        bookRepository.findById(BOOK_ID_1);

        // When
        mockMvc.perform(
                delete("/api/books/{id}", BOOK_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        Optional<Book> deletedBook = bookRepository.findById(BOOK_ID_1);
        Assertions.assertFalse(deletedBook.isPresent());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Search books with valid query parameters
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchBooksByParameters_ValidQuery_ReturnsExpectedResults() throws Exception {
        // Given
        List<BookDto> expected = new ArrayList<>();
        BookDto expectedBook = createBookDto(
                BOOK_ID_1, BOOK_SEARCH_TITLE,
                BOOK_SEARCH_AUTHOR, BOOK_ISBN_1,
                BOOK_PRICE_2, BOOK_COVER_IMAGE_1,
                BOOK_DESCRIPTION_1, Collections.emptyList());
        expected.add(expectedBook);

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/books/search")
                                .param("title", BOOK_SEARCH_TITLE)
                                .param("author", BOOK_SEARCH_AUTHOR)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actualArray = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto[].class);
        List<BookDto> actualList = Arrays.stream(actualArray).collect(Collectors.toList());
        Assertions.assertIterableEquals(expected, actualList);
    }

    private BookDto createBookDto(
            Long id, String title, String author,
            String isbn, BigDecimal price, String coverImage,
            String description, List<Long> categoriesIds) {
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setIsbn(isbn);
        bookDto.setPrice(price);
        bookDto.setCoverImage(coverImage);
        bookDto.setDescription(description);
        bookDto.setCategoriesIds(categoriesIds);
        return bookDto;
    }

    private Category createCategory(
            String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    private List<BookDto> prepareExpectedBooks() {
        List<BookDto> expected = new ArrayList<>();
        expected.add(createBookDto(BOOK_ID_1, "Harry Potter", "Rowling", "123456789",
                BigDecimal.valueOf(19.99), "https://randomImage1.jpg", "great book", Collections.emptyList()));
        expected.add(createBookDto(BOOK_ID_2, "Lord of the rings", "Tolkien", "987654321",
                BigDecimal.valueOf(25.99), "https://randomImage2.jpg", "great book", Collections.emptyList()));
        expected.add(createBookDto(BOOK_ID_3, "Game of thrones", "Martin", "555666777",
                BigDecimal.valueOf(29.99), "https://randomImage3.jpg", "great book", Collections.emptyList()));
        return expected;
    }
}
