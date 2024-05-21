package mate.academy.springbootintro.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springbootintro.dto.AddBookToCartRequest;
import mate.academy.springbootintro.dto.CartItemDto;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {

    protected static MockMvc mockMvc;
    private static final String DEFAULT_USER_EMAIL = "user@example.com";
    private static final String BOOK_TITLE = "Game of thrones";
    private static final int QUANTITY = 25;
    private static final Long USER_ID = 2L;
    private static final Long BOOK_ID = 3L;
    private static final Long CART_ITEM_ID = 1L;
    private static final Long CART_ITEM_DTO_ID = 1L;
    private static final Long SHOPPING_CART_ID = 2L;
    private static final Long SHOPPING_CART_DTO_ID = 2L;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-3-books-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cart_items/add_items_to_cart_1.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cart_items/" +
                            "delete-cart-items-from-cart_items-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-books-from-books-table.sql")
            );
        }
    }

    @Test
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Add Item to Cart
            """)
    void addToCart_ValidRequestDto_AddsItemToCartSuccessfully() throws Exception {
        // Given
        AddBookToCartRequest request = new AddBookToCartRequest(BOOK_ID, QUANTITY);
        String jsonRequest = objectMapper.writeValueAsString(request);

        CartItemDto expectedCartItem =
                createCartItemDto(CART_ITEM_DTO_ID, BOOK_ID, BOOK_TITLE, QUANTITY);

        Set<CartItemDto> expectedCartItems = new HashSet<>();
        expectedCartItems.add(expectedCartItem);

        ShoppingCartDto expected =
                createShoppingCartDto(SHOPPING_CART_DTO_ID, USER_ID, expectedCartItems);

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

        assertThat(actual.userId()).isEqualTo(expected.userId());
        assertThat(actual.cartItems()).containsExactlyInAnyOrderElementsOf(expected.cartItems());
    }

    @Test
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Remove Cart Item
            """)
    void removeCartItem_ValidUserAndCartItemId_ReturnsUpdatedShoppingCartDto()
            throws Exception {
        MvcResult result = mockMvc.perform(
                        delete("/api/cart/cart-items/{cartItemId}", CART_ITEM_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithUserDetails(DEFAULT_USER_EMAIL)
    @DisplayName("""
            Get Shopping Cart
            """)
    void getShoppingCart_ValidUser_ReturnsShoppingCartDto() throws Exception {
        // Given
        User user = createUser(USER_ID);

        Book book = createBook(BOOK_ID, BOOK_TITLE);

        ShoppingCart shoppingCart = createShoppingCart(SHOPPING_CART_ID, user, Set.of());

        CartItem cartItem = createCartItem(CART_ITEM_ID, shoppingCart, book, QUANTITY);
        shoppingCart.setCartItems(Set.of(cartItem));

        CartItemDto cartItemDto = createCartItemDto(CART_ITEM_DTO_ID, BOOK_ID, BOOK_TITLE, QUANTITY);

        Set<CartItemDto> cartItemDtos = Set.of(cartItemDto);

        ShoppingCartDto expected
                = createShoppingCartDto(SHOPPING_CART_DTO_ID, USER_ID, cartItemDtos);

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

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    private Book createBook(Long bookId, String title) {
        Book book = new Book();
        book.setId(bookId);
        book.setTitle(title);
        return book;
    }

    private CartItem createCartItem(
            Long cartItemId, ShoppingCart shoppingCart,
            Book book, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    private ShoppingCart createShoppingCart(
            Long shoppingCartId, User user,
            Set<CartItem> cartItems) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(shoppingCartId);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(cartItems);
        return shoppingCart;
    }

    private CartItemDto createCartItemDto(
            Long cartItemDtoId, Long bookId,
            String bookTitle, int quantity) {
        return new CartItemDto(cartItemDtoId, bookId, bookTitle, quantity);
    }

    private ShoppingCartDto createShoppingCartDto(
            Long shoppingCartDtoId, Long userId,
            Set<CartItemDto> cartItems) {
        return new ShoppingCartDto(shoppingCartDtoId, userId, cartItems);
    }
}
