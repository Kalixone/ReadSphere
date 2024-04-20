package mate.academy.springbootintro.model;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    private Long id;
    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "shoppingCart")
    private Set<CartItem> cartItems;
}
