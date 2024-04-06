package mate.academy.springbootintro.repository.shoppingcart;

import mate.academy.springbootintro.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT cart FROM ShoppingCart cart "
            + "LEFT JOIN FETCH cart.user "
            + "LEFT JOIN FETCH cart.cartItems items "
            + "LEFT JOIN FETCH items.book "
            + "WHERE :userId = cart.user.id ")
    Optional<ShoppingCart> findShoppingCartByUserId(Long userId);
}
