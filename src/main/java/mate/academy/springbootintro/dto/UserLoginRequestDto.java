package mate.academy.springbootintro.dto;

import jakarta.validation.constraints.NotEmpty;
import mate.academy.springbootintro.validation.Email;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotEmpty
        @Email
        String email,
        @NotEmpty
        @Length(min = 8, max = 20)
        String password
) {
}
