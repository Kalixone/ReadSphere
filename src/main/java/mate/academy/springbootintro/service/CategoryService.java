package mate.academy.springbootintro.service;

import mate.academy.springbootintro.dto.CategoryDto;
import mate.academy.springbootintro.dto.CreateCategoryRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
