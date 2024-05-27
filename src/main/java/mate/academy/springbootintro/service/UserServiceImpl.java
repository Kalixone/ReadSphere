package mate.academy.springbootintro.service;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.UserResponseDto;
import mate.academy.springbootintro.exception.RegistrationException;
import mate.academy.springbootintro.mapper.UserMapper;
import mate.academy.springbootintro.model.Role;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.role.RoleRepository;
import mate.academy.springbootintro.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setShippingAddress(requestDto.shippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }
}
