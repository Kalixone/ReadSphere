package mate.academy.springbootintro.service;

import mate.academy.springbootintro.dto.AddBookToCartRequest;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.dto.UpdateBookQuantity;

public interface ShoppingCartService {
    ShoppingCartDto addToCart(Long id, AddBookToCartRequest request);

    ShoppingCartDto updateCartItemQuantity(Long id, Long cartItemId, UpdateBookQuantity request);

    ShoppingCartDto getShoppingCartByUserId(Long id);

    ShoppingCartDto removeBook(Long id, Long cartItemId);
}
