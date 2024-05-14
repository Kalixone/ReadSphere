    package mate.academy.springbootintro.service;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.mockito.Mockito.doNothing;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;
    import mate.academy.springbootintro.dto.AddBookToCartRequest;
    import mate.academy.springbootintro.dto.CartItemDto;
    import mate.academy.springbootintro.dto.ShoppingCartDto;
    import mate.academy.springbootintro.mapper.ShoppingCartMapper;
    import mate.academy.springbootintro.model.Book;
    import mate.academy.springbootintro.model.CartItem;
    import mate.academy.springbootintro.model.ShoppingCart;
    import mate.academy.springbootintro.model.User;
    import mate.academy.springbootintro.repository.book.BookRepository;
    import mate.academy.springbootintro.repository.cartitem.CartItemRepository;
    import mate.academy.springbootintro.repository.shoppingcart.ShoppingCartRepository;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.mockito.junit.jupiter.MockitoExtension;
    import java.util.Optional;
    import java.util.Set;

    @ExtendWith(MockitoExtension.class)
    public class ShoppingCartServiceTest {

        private static final Long USER_ID = 1L;
        private static final Long BOOK_ID = 10L;
        private static final Long SHOPPING_CART_ID = 1L;
        private static final Long CART_ITEM_ID = 10L;
        private static final int QUANTITY = 5;
        private static final String BOOK_TITLE = "Test Book";

        @InjectMocks
        private ShoppingCartServiceImpl shoppingCartService;

        @Mock
        private ShoppingCartRepository shoppingCartRepository;

        @Mock
        private ShoppingCartMapper shoppingCartMapper;

        @Mock
        private BookRepository bookRepository;

        @Mock
        private CartItemRepository cartItemRepository;

        @Test
        @DisplayName("""
                Verify addToCart() method works
                """)
        public void addToCart_ValidRequestDto_ReturnsShoppingCartDto() {
            // Given
            AddBookToCartRequest request = new AddBookToCartRequest(
                    BOOK_ID,
                    QUANTITY
            );

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setId(SHOPPING_CART_ID);

            User user = new User();
            user.setId(USER_ID);
            shoppingCart.setUser(user);

            Book book = new Book();
            book.setId(BOOK_ID);
            book.setTitle(BOOK_TITLE);

            CartItem cartItem = new CartItem();
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
            cartItem.setQuantity(QUANTITY);

            CartItemDto cartItemDto = new CartItemDto(
                    cartItem.getId(),
                    cartItem.getBook().getId(),
                    cartItem.getBook().getTitle(),
                    cartItem.getQuantity()
            );

            Set<CartItemDto> expectedCartItems = Set.of(cartItemDto);

            ShoppingCartDto expected = new ShoppingCartDto(
                    shoppingCart.getId(),
                    shoppingCart.getUser().getId(),
                    expectedCartItems
            );

            when(shoppingCartRepository
                    .findShoppingCartByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
            when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
            when(cartItemRepository.save(Mockito.any(CartItem.class))).thenReturn(cartItem);
            when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

            // When
            ShoppingCartDto actual = shoppingCartService.addToCart(USER_ID, request);

            // Then
            assertEquals(expected, actual);
            verify(shoppingCartRepository, times(2)).findShoppingCartByUserId(USER_ID);
            verify(bookRepository, times(1)).findById(BOOK_ID);
            verify(cartItemRepository).save(Mockito.any(CartItem.class));
            verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        }

        @Test
        @DisplayName("""
                Verify getShoppingCartByUserId() method works
                """)
        public void getShoppingCartByUserId_ValidUserId_ReturnsShoppingCartDto() {
            // Given
            User user = new User();
            user.setId(USER_ID);

            Book book = new Book();
            book.setId(BOOK_ID);
            book.setTitle(BOOK_TITLE);

            CartItem cartItem1 = new CartItem();
            cartItem1.setId(CART_ITEM_ID);
            cartItem1.setBook(book);
            cartItem1.setQuantity(QUANTITY);

            ShoppingCart shoppingCart = new ShoppingCart();
            cartItem1.setShoppingCart(shoppingCart);
            shoppingCart.setId(SHOPPING_CART_ID);
            shoppingCart.setUser(user);
            shoppingCart.setCartItems(Set.of(cartItem1));

            CartItemDto cartItemDto1 = new CartItemDto(
                    cartItem1.getId(),
                    cartItem1.getBook().getId(),
                    cartItem1.getBook().getTitle(),
                    cartItem1.getQuantity()
            );

            Set<CartItemDto> cartItemDtos = Set.of(cartItemDto1);

            ShoppingCartDto expected = new ShoppingCartDto(
                    shoppingCart.getId(),
                    shoppingCart.getUser().getId(),
                    cartItemDtos
            );

            when(shoppingCartRepository
                    .findShoppingCartByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
            when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

            // When
            ShoppingCartDto actual = shoppingCartService.getShoppingCartByUserId(USER_ID);

            // Then
            assertEquals(expected, actual);
            verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
            verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(USER_ID);
        }

        @Test
        @DisplayName("""
                Verify removeBook() method removes book from cart
                """)
        public void removeBook_ValidIdAndCartItemId_ReturnsUpdatedShoppingCartDto() {
            // Given:
            User user = new User();
            user.setId(USER_ID);

            Book book = new Book();
            book.setId(BOOK_ID);
            book.setTitle(BOOK_TITLE);

            CartItem cartItem = new CartItem();
            cartItem.setId(CART_ITEM_ID);
            cartItem.setBook(book);
            cartItem.setQuantity(QUANTITY);

            ShoppingCart shoppingCart = new ShoppingCart();
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.setId(SHOPPING_CART_ID);
            shoppingCart.setUser(user);
            shoppingCart.setCartItems(Set.of(cartItem));

            CartItemDto cartItemDto = new CartItemDto(
                    cartItem.getId(),
                    cartItem.getBook().getId(),
                    cartItem.getBook().getTitle(),
                    cartItem.getQuantity()
            );

           Set<CartItemDto> cartItemDtos = Set.of(cartItemDto);

            ShoppingCartDto expected = new ShoppingCartDto(
                    shoppingCart.getId(),
                    shoppingCart.getUser().getId(),
                    cartItemDtos
            );

            when(shoppingCartRepository
                    .findShoppingCartByUserId(USER_ID)).thenReturn(Optional.of(shoppingCart));
            doNothing().when(cartItemRepository).deleteById(CART_ITEM_ID);
            when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

            // When
            ShoppingCartDto actual = shoppingCartService.removeBook(USER_ID, CART_ITEM_ID);

            // Then
            assertEquals(expected, actual);

            verify(cartItemRepository, times(1)).deleteById(CART_ITEM_ID);
            verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
            verify(shoppingCartRepository, times(1)).findShoppingCartByUserId(USER_ID);
        }
    }
