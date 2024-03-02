package mate.academy.springbootintro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mate.academy.springbootintro.validation.FieldMatch;
import org.hibernate.validator.constraints.Length;

@FieldMatch(first = "password", second = "repeatPassword", message = "Password must match")
public record UserRegistrationRequestDto(
        @Email
        String email,
        @NotBlank
        @Length(min = 8, max = 20)
        String password,
        @NotBlank
        @Length(min = 8, max = 20)
        String repeatPassword,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        String shippingAddress
) {
}
