package mate.academy.springbootintro.dto;

import jakarta.validation.constraints.NotNull;
import mate.academy.springbootintro.model.Order;

public record UpdateOrderStatusRequest(
        @NotNull
        Order.Status status
) {
}
