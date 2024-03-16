package mate.academy.springbootintro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.UserLoginRequestDto;
import mate.academy.springbootintro.dto.UserLoginResponseDto;
import mate.academy.springbootintro.dto.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.UserResponseDto;
import mate.academy.springbootintro.exception.RegistrationException;
import mate.academy.springbootintro.security.AuthenticationService;
import mate.academy.springbootintro.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserResponseDto registerUser
            (@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
