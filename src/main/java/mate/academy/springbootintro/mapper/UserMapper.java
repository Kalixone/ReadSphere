package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.UserResponseDto;
import mate.academy.springbootintro.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User requestDto);
}
