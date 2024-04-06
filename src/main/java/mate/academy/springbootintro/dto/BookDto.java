package mate.academy.springbootintro.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private List<Long> categoriesIds;
}
