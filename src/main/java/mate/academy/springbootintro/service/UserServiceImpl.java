package mate.academy.springbootintro.service;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.UserResponseDto;
import mate.academy.springbootintro.exception.RegistrationException;
import mate.academy.springbootintro.mapper.UserMapper;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register
            (UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        User user = new User();
        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPassword(requestDto.password());
        user.setShippingAddress(requestDto.shippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}
