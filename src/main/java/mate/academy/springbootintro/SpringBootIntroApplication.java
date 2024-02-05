package mate.academy.springbootintro;

import java.math.BigDecimal;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootIntroApplication {

	@Autowired
	private BookService bookService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootIntroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				Book book = new Book();
				book.setTitle("Lalka");
				book.setAuthor("Bolesław Prus");
				book.setIsbn("0-123-45678-9");
				book.setPrice(new BigDecimal(25L));
				book.setDescription("Realistic novel");
				book.setCoverImage("City Image");

				bookService.save(book);
				System.out.println(bookService.findAll());
			}
		};
	}
}
