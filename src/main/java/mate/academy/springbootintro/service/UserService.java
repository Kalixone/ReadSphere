package mate.academy.springbootintro.service;

import mate.academy.springbootintro.dto.UserRegistrationRequestDto;
import mate.academy.springbootintro.dto.UserResponseDto;
import mate.academy.springbootintro.exception.RegistrationException;

public interface UserService {

   UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
