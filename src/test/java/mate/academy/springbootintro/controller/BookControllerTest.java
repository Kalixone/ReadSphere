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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    protected static MockMvc mockMvc;

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
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
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
            "classpath:database/books_categories/" +
                    "delete-book_id-and_category_id-from-books_categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/categories/delete-categories-from-categories-table.sql"

    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_CreatesNewBook() throws Exception {
        // Given
        Category category = new Category();
        category.setName("magic");
        category.setDescription("dragons");

        categoryRepository.save(category);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Lalka",
                "Fredryk",
                BigDecimal.TEN,
                "http://example.com/randomImage.jpg",
                "good book",
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
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get all books
            """)
    @Sql(scripts = "classpath:database/books/add-3-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_ValidRequest_ReturnsAllBooks() throws Exception {
        // Given
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("Harry Potter");
        book1.setAuthor("Rowling");
        book1.setIsbn("123456789");
        book1.setPrice(BigDecimal.valueOf(19.99));
        book1.setCoverImage("randomImage1.jpg");
        book1.setDescription("great book");
        book1.setCategoriesIds(Collections.emptyList());

        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("Władca Pierścieni");
        book2.setAuthor("Tolkien");
        book2.setIsbn("987654321");
        book2.setPrice(BigDecimal.valueOf(25.99));
        book2.setCoverImage("randomImage2.jpg");
        book2.setDescription("great book");
        book2.setCategoriesIds(Collections.emptyList());

        BookDto book3 = new BookDto();
        book3.setId(3L);
        book3.setTitle("Gra o Tron");
        book3.setAuthor("Martin");
        book3.setIsbn("555666777");
        book3.setPrice(BigDecimal.valueOf(29.99));
        book3.setCoverImage("randomImage3.jpg");
        book3.setDescription("great book");
        book3.setCategoriesIds(Collections.emptyList());

        List<BookDto> expected = new ArrayList<>();
        expected.add(book1);
        expected.add(book2);
        expected.add(book3);

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
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get book by ID
            """)
    @Sql(scripts = "classpath:database/books/add-3-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_ValidId_ReturnsBookDto() throws Exception {
        // Given
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Harry Potter");
        bookDto.setAuthor("Rowling");
        bookDto.setIsbn("123456789");
        bookDto.setPrice(BigDecimal.valueOf(19.99));
        bookDto.setCoverImage("randomImage1.jpg");
        bookDto.setDescription("great book");
        bookDto.setCategoriesIds(Collections.emptyList());

        BookDto expected = new BookDto();
        expected.setId(bookDto.getId());
        expected.setTitle(bookDto.getTitle());
        expected.setAuthor(bookDto.getAuthor());
        expected.setIsbn(bookDto.getIsbn());
        expected.setPrice(bookDto.getPrice());
        expected.setCoverImage(bookDto.getCoverImage());
        expected.setDescription(bookDto.getDescription());
        expected.setCategoriesIds(bookDto.getCategoriesIds());

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Delete book by ID
            """)
    @Sql(scripts = "classpath:database/books/add-3-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookById_ValidId_DeletesBook() throws Exception {
        // Given
        bookRepository.findById(1L);

        // When
        mockMvc.perform(
                delete("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        Optional<Book> deletedBook = bookRepository.findById(1L);
        Assertions.assertFalse(deletedBook.isPresent());

    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Update book with specific ID
            """)
    @Sql(scripts = "classpath:database/books/add-3-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchBooksByParameters_ValidQuery_ReturnsExpectedResults() throws Exception {
        // Given
        String searchTitle = "Harry Potter";
        String searchAuthor = "Rowling";

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/books/search")
                                .param("title", searchTitle)
                                .param("author", searchAuthor)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto[].class);

        Assertions.assertTrue(Arrays.stream(actual)
                        .allMatch(book -> book.getTitle().contains(searchTitle)
                                && book.getAuthor().equals(searchAuthor)),
                "Search results do not contain the expected books");
    }
}
