package mate.academy.springbootintro.repository;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import mate.academy.springbootintro.config.CustomMySqlContainer;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {

    private static final Long USER_ID = 3L;
    private static final String SPRING_DATASOURCE_URL =
            "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME =
            "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD =
            "spring.datasource.password";
    private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME =
            "spring.datasource.driver-class-name";

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

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

    @Test
    @DisplayName("""
            Verify findShoppingCartByUserId() method works
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/users/add-1-user-to-users-table.sql",
            "classpath:database/shopping_carts/add-user-1-shopping-cart.sql",
            "classpath:database/books/add-3-books-to-books-table.sql",
            "classpath:database/cart_items/add_items_to_cart_1.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items-from-cart_items-table.sql",
            "classpath:database/shopping_carts/delete-shopping-cart-from-shopping_carts-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/users/delete-user-from-users-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUserId_ValidUserId_ReturnsShoppingCart() {
        // When
        Optional<ShoppingCart> shoppingCartByUserId =
                shoppingCartRepository.findShoppingCartByUserId(USER_ID);

        // Then
        assertTrue(shoppingCartByUserId.isPresent(),
                "Shopping cart should be present for given user ID");

        ShoppingCart shoppingCart = shoppingCartByUserId.get();
        assertNotNull(shoppingCart);
        assertEquals(USER_ID, shoppingCart.getUser().getId());

        assertFalse(shoppingCart.isDeleted());
        assertNotNull(shoppingCart.getCartItems());
    }
}
