package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.CartItemDto;
import mate.academy.springbootintro.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);
}
