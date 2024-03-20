package mate.academy.springbootintro.repository.book;

import mate.academy.springbootintro.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories c"
            + "WHERE :categoryId IN (SELECT category.id FROM b.categories category)")
    List<Book> findAllByCategoryId(Long categoryId);
}
