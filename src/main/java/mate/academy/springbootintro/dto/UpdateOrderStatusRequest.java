package mate.academy.springbootintro.dto;

import mate.academy.springbootintro.model.Order;

public record UpdateOrderStatusRequest(
        Order.Status status
) {
}
