package mate.academy.springbootintro.service;

import mate.academy.springbootintro.dto.OrderDto;
import mate.academy.springbootintro.dto.OrderItemDto;
import mate.academy.springbootintro.dto.OrderRequest;
import mate.academy.springbootintro.dto.UpdateOrderStatusRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long userId, OrderRequest request);

    List<OrderDto> getUserOrderHistory(Pageable pageable, Long userId);

    OrderDto updateOrder(Long userId, Long orderId, UpdateOrderStatusRequest request);

    List<OrderItemDto> getOrderItems(Pageable pageable, Long userId, Long orderId);

    OrderItemDto getSpecificOrderItem(Long userId, Long orderId, Long orderItemId);
}
