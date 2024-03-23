package mate.academy.springbootintro.service;

import lombok.RequiredArgsConstructor;
import mate.academy.springbootintro.dto.CategoryDto;
import mate.academy.springbootintro.dto.CreateCategoryRequestDto;
import mate.academy.springbootintro.exception.EntityNotFoundException;
import mate.academy.springbootintro.mapper.CategoryMapper;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find category by id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto) {
        Category entity = categoryMapper.toEntity(createCategoryRequestDto);
        return categoryMapper.toDto(categoryRepository.save(entity));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(categoryDto.name());
        existingCategory.setDescription(categoryDto.description());
        existingCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(existingCategory);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find category by id: " + id));
    }
}
