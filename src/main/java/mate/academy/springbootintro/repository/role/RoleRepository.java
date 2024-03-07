package mate.academy.springbootintro.repository.role;

import mate.academy.springbootintro.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
