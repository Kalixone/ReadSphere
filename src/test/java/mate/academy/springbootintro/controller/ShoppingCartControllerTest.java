package mate.academy.springbootintro.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootintro.config.CustomMySqlContainer;
import mate.academy.springbootintro.dto.AddBookToCartRequest;
import mate.academy.springbootintro.dto.CartItemDto;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.cartitem.CartItemRepository;
import mate.academy.springbootintro.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {

    protected static MockMvc mockMvc;
    private static final String SPRING_DATASOURCE_URL =
            "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME =
            "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD =
            "spring.datasource.password";
    private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME =
            "spring.datasource.driver-class-name";
    private static final String DEFAULT_USER_EMAIL = "user@example.com";
    private static final int QUANTITY = 25;
    private static final Long USER_ID = 2L;
    private static final Long BOOK_ID = 3L;
    private static final Long CART_ITEM_ID = 1L;
    private static final Long CART_ITEM_DTO_ID = 1L;
    private static final Long SHOPPING_CART_ID = 2L;
    private static final Long SHOPPING_CART_DTO_ID = 2L;
    private static final String EXPECTED_BOOK_TITLE = "Game of thrones";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Add Item to Cart
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql",
            "classpath:database/cart_items/add_items_to_cart_1.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items-from-cart_items-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addToCart_ValidRequestDto_AddsItemToCartSuccessfully() throws Exception {
        // Given
        AddBookToCartRequest request = new AddBookToCartRequest(BOOK_ID, QUANTITY);
        String jsonRequest = objectMapper.writeValueAsString(request);

        CartItemDto expectedCartItem =
                new CartItemDto(CART_ITEM_DTO_ID, BOOK_ID, EXPECTED_BOOK_TITLE, QUANTITY);
        Set<CartItemDto> expectedCartItems = new HashSet<>();
        expectedCartItems.add(expectedCartItem);

        ShoppingCartDto expected =
                new ShoppingCartDto(SHOPPING_CART_DTO_ID, USER_ID, expectedCartItems);

        // When
        MvcResult result = mockMvc.perform(
                        post("/api/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartDto actual = objectMapper.readValue
                (result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(actual);
        assertEquals(expected.userId(), actual.userId());
        assertFalse(actual.cartItems().isEmpty());

        for (CartItemDto expectedItem : expected.cartItems()) {
            CartItemDto actualItem = actual.cartItems().stream()
                    .filter(item -> item.bookId().equals(expectedItem.bookId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Item with bookId " +
                            expectedItem.bookId() + " was not found in the cart"));

            assertEquals(expectedItem.bookId(), actualItem.bookId());
            assertEquals(expectedItem.bookTitle(), actualItem.bookTitle());
            assertEquals(expectedItem.quantity(), actualItem.quantity());
        }
    }

    @Test
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Remove Cart Item
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql",
            "classpath:database/cart_items/add_items_to_cart_1.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items-from-cart_items-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void removeCartItem_ValidUserAndCartItemId_ReturnsUpdatedShoppingCartDto() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        delete("/api/cart/cart-items/{cartItemId}", CART_ITEM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        // Then
        Optional<CartItem> deletedCartItem = cartItemRepository.findById(CART_ITEM_ID);
        Assertions.assertFalse(deletedCartItem.isPresent());
    }

    @Test
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Get Shopping Cart
            """)
    @Sql(scripts = {
            "classpath:database/books/delete-books-from-books-table.sql",
            "classpath:database/books/add-3-books-to-books-table.sql",
            "classpath:database/cart_items/add_items_to_cart_1.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cart_items/delete-cart-items-from-cart_items-table.sql",
            "classpath:database/books/delete-books-from-books-table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getShoppingCart_ValidUser_ReturnsShoppingCartDto() throws Exception {
        // Given
        User user = new User();
        user.setId(USER_ID);

        Book book = new Book();
        book.setId(BOOK_ID);
        book.setTitle(EXPECTED_BOOK_TITLE);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(SHOPPING_CART_ID);
        shoppingCart.setUser(user);

        CartItem cartItem = new CartItem();
        cartItem.setId(CART_ITEM_ID);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(QUANTITY);
        shoppingCart.setCartItems(Set.of(cartItem));

        CartItemDto cartItemDto = new CartItemDto(
                cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                cartItem.getQuantity()
        );

        Set<CartItemDto> cartItemDtos = Set.of(cartItemDto);

        ShoppingCartDto expected = new ShoppingCartDto(
                SHOPPING_CART_ID,
                USER_ID,
                cartItemDtos
        );

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartDto actual = objectMapper.readValue
                (result.getResponse().getContentAsString(), ShoppingCartDto.class);

        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }
}
