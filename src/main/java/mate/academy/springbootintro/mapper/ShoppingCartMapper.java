package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.ShoppingCartDto;
import mate.academy.springbootintro.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
