package mate.academy.springbootintro.repository;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import mate.academy.springbootintro.repository.book.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import mate.academy.springbootintro.model.Book;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {

    private static final Long CATEGORY_ID = 2L;
    private static final String EXPECTED_BOOK_TITLE_1 = "Harry Potter";
    private static final String EXPECTED_BOOK_TITLE_2 = "Game of thrones";
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Verify findAllByCategoryId() method works
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/categories/delete-categories-from-categories-table.sql",
            "classpath:database/categories/add-2-categories-to-categories-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql",
            "classpath:database/books_categories/" +
                    "add-book_id-and-category_id-to-books_categories-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/books_categories/" +
                    "delete-book_id-and_category_id-from-books_categories-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/categories/delete-categories-from-categories-table.sql"

    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnsBooks() {
        // When
        List<Book> books = bookRepository.findAllByCategoryId(CATEGORY_ID);

        // Then
        assertNotNull(books);
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(book -> book.getTitle().equals(EXPECTED_BOOK_TITLE_1)));
        assertTrue(books.stream().anyMatch(book -> book.getTitle().equals(EXPECTED_BOOK_TITLE_2)));
    }
}
