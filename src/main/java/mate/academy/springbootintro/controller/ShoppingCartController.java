package mate.academy.springbootintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.AddBookToCartRequest;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.dto.UpdateBookQuantity;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart management", description = "Endpoints for managing Shopping Cart")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Add Item to Cart",
            description = "Add a new item to the user's shopping cart.")
    public ShoppingCartDto addToCart(Authentication authentication,
            @RequestBody AddBookToCartRequest request) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addToCart(user.getId(), request);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove Item from Cart",
            description = "Remove an item from the user's shopping cart.")
    public ShoppingCartDto removeCartItem(Authentication authentication,
            @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.removeBook(user.getId(), cartItemId);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Update Item Quantity in Cart",
            description = "Update the quantity of an item in the user's shopping cart.")
    public ShoppingCartDto updateCartItemQuantity(Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody UpdateBookQuantity request) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItemQuantity(user.getId(), cartItemId, request);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Operation(summary = "Get User Shopping Cart",
            description = "Retrieve the shopping cart from current user.")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartByUserId(user.getId());
    }
}
