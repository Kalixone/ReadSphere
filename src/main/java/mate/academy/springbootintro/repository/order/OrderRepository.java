package mate.academy.springbootintro.repository.order;

import mate.academy.springbootintro.model.Order;
import mate.academy.springbootintro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
