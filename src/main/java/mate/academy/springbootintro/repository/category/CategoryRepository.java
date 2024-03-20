package mate.academy.springbootintro.repository.category;

import mate.academy.springbootintro.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
