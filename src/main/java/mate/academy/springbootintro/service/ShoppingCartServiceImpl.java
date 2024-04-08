package mate.academy.springbootintro.service;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.AddBookToCartRequest;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.dto.UpdateBookQuantity;
import mate.academy.springbootintro.exception.EntityNotFoundException;
import mate.academy.springbootintro.mapper.ShoppingCartMapper;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.repository.book.BookRepository;
import mate.academy.springbootintro.repository.cartitem.CartItemRepository;
import mate.academy.springbootintro.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbootintro.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;
    private final CartItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ShoppingCartDto addToCart(Long id, AddBookToCartRequest request) {
        ShoppingCart shoppingCart = getShoppingCart(id);

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(request.quantity());
        itemRepository.save(cartItem);

        shoppingCart = getShoppingCart(id);

        ShoppingCartDto shoppingCartDto = cartMapper.toDto(shoppingCart);

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItemQuantity(Long id, Long cartItemId,
                                                  UpdateBookQuantity request) {
        itemRepository.updateCartItemQuantityById(request.quantity(), cartItemId);
        return cartMapper.toDto(getShoppingCart(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCartByUserId(Long id) {
        return cartMapper.toDto(getShoppingCart(id));
    }

    @Override
    public ShoppingCartDto removeBook(Long id, Long cartItemId) {
        itemRepository.deleteById(cartItemId);
        return cartMapper.toDto(getShoppingCart(id));
    }

    private ShoppingCart getShoppingCart(Long userId) {
        return cartRepository.findShoppingCartByUserId(userId).orElseGet(
                () -> {
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("Can't find "
                                    + "user by userId: " + userId)));
                    return cartRepository.save(shoppingCart);
                });
    }
}
