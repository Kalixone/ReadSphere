package mate.academy.springbootintro.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateBookRequestDto {
    private String title;
    private String author;
    private BigDecimal price;
    private String coverImage;
    private String description;
}
