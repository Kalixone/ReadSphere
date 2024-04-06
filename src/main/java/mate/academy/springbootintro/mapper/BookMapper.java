package mate.academy.springbootintro.mapper;

import mate.academy.springbootintro.config.MapperConfig;
import mate.academy.springbootintro.dto.BookDto;
import mate.academy.springbootintro.dto.CreateBookRequestDto;
import mate.academy.springbootintro.dto.UpdateBookRequestDto;
import mate.academy.springbootintro.dto.BookDtoWithoutCategoryIds;
import mate.academy.springbootintro.model.Book;
import mate.academy.springbootintro.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);

    void updateModel(UpdateBookRequestDto updateBookRequestDto, @MappingTarget Book book);

    Book toEntity (CreateBookRequestDto bookDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        List<Long> ids = book.getCategories().stream()
                .map(Category::getId)
                .toList();
        bookDto.setCategoriesIds(ids);
    }
}
