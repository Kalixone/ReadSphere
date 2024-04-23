package mate.academy.springbootintro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.OrderItemDto;
import mate.academy.springbootintro.dto.OrderRequest;
import mate.academy.springbootintro.dto.OrderDto;
import mate.academy.springbootintro.dto.UpdateOrderStatusRequest;
import mate.academy.springbootintro.exception.EntityNotFoundException;
import mate.academy.springbootintro.mapper.OrderItemMapper;
import mate.academy.springbootintro.mapper.OrderMapper;
import mate.academy.springbootintro.model.Order;
import mate.academy.springbootintro.model.ShoppingCart;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.model.CartItem;
import mate.academy.springbootintro.model.OrderItem;
import mate.academy.springbootintro.repository.cartitem.CartItemRepository;
import mate.academy.springbootintro.repository.order.OrderRepository;
import mate.academy.springbootintro.repository.orderitem.OrderItemRepository;
import mate.academy.springbootintro.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbootintro.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepostiory;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public OrderDto placeOrder(Long userId, OrderRequest request) {
        User user = userRepository.getReferenceById(userId);
        ShoppingCart cart = shoppingCartRepository.findShoppingCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException
                        ("Shopping cart not found for user with id: " + userId));
        Order order = convertCartToOrder(cart, request.shippingAddress());
        Order savedOrder = orderRepostiory.save(order);
        cartItemRepository.deleteCartItemsByShoppingCartId(userId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderDto> getUserOrderHistory(Pageable pageable, Long userId) {
        User user = userRepository.getReferenceById(userId);
        List<Order> orders = orderRepostiory.findByUser(pageable, user);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto updateOrder(Long userId, Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepostiory.getReferenceById(orderId);
        order.setStatus(request.status());
        return orderMapper.toDto(orderRepostiory.save(order));
    }

    @Override
    public List<OrderItemDto> getOrderItems(Pageable pageable, Long userId, Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId, pageable);
        return orderItems.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemDto getSpecificOrderItem(Long userId, Long orderId, Long orderItemId) {
        Order order = orderRepostiory.getReferenceById(orderId);
        Set<OrderItem> orderItems = order.getOrderItems();
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException
                        ("OrderItem not found with id: " + orderItemId));

        return orderItemMapper.toDto(orderItem);
    }

    private Order convertCartToOrder(ShoppingCart cart, String shippingAddress) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);

        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal price = cartItem.getBook().getPrice();
            orderItem.setPrice(price != null ? price.multiply
                    (BigDecimal.valueOf(cartItem.getQuantity())) : null);

            orderItems.add(orderItem);
        }

        BigDecimal total = orderItems.stream()
                .filter(orderItem -> orderItem.getPrice() != null)
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);

        order.setOrderItems(orderItems);

        return order;
    }
}
