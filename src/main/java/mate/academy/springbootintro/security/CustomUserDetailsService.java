package mate.academy.springbootintro.security;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.model.User;
import mate.academy.springbootintro.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user by email"));
    }
}



