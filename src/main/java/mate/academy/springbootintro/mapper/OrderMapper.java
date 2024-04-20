package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.OrderDto;
import mate.academy.springbootintro.dto.OrderItemDto;
import mate.academy.springbootintro.model.Order;
import mate.academy.springbootintro.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems", source = "orderItems")
    OrderDto toDto(Order order);

    default Set<OrderItemDto> mapOrderItems(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> new OrderItemDto(orderItem.getId(),
                        orderItem.getBook().getId(), orderItem.getQuantity()))
                .collect(Collectors.toSet());
    }
}
