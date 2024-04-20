package mate.academy.springbootintro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.OrderRequest;
import mate.academy.springbootintro.dto.OrderDto;
import mate.academy.springbootintro.dto.OrderItemDto;
import mate.academy.springbootintro.dto.UpdateOrderStatusRequest;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Tag(name = "Order management", description = "Endpoints for managing Orders")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Add Order", description = "Add a new order for the user")
    OrderDto addOrder(Authentication authentication,
                      @RequestBody OrderRequest request) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), request);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    @Operation(summary = "Get Order History", description = "Get the order history for the user")
    List<OrderDto> getOrderHistory(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getUserOrderHistory(user.getId());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("{id}")
    @Operation(summary = "Update Order Status", description = "Update the status of an order")
    OrderDto updateOrderStatus(Authentication authentication,
                               @PathVariable Long id,
                               @RequestBody UpdateOrderStatusRequest request) {
        User user = (User) authentication.getPrincipal();
        return orderService.updateOrder(user.getId(), id, request);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get Order Items", description = "Get the items for a specific order")
    List<OrderItemDto> getOrderItems(Authentication authentication,
                                     @PathVariable Long orderId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItems(user.getId(), orderId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/{orderId}/items/{orderItemId}")
    @Operation(summary = "Get Order Item", description = "Get a specific item from an order")
    OrderItemDto getSpecificOrderItem(Authentication authentication,
                                      @PathVariable Long orderId,
                                      @PathVariable Long orderItemId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getSpecificOrderItem(user.getId(), orderId, orderItemId);
    }
}
