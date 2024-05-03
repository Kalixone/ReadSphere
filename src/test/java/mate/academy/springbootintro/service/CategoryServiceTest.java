package mate.academy.springbootintro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import mate.academy.springbootintro.dto.CategoryDto;
import mate.academy.springbootintro.dto.CreateCategoryRequestDto;
import mate.academy.springbootintro.mapper.CategoryMapper;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("""
            Verify save() method works
            """)
    public void createCategory_ValidRequestDto_ReturnsCategoryDto() {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Horor",
                "Scary"
        );

        Category category = new Category();
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());

        CategoryDto categoryDto = new CategoryDto(
                1L,
                category.getName(),
                category.getDescription()
        );

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        when(categoryRepository.save(category)).thenReturn(category);

        // When
        CategoryDto savedCategoryDto = categoryService.save(requestDto);

        // Then
        assertThat(savedCategoryDto).isEqualTo(categoryDto);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("""
            Verify findAll() method works
            """)
    public void findAll_ValidPageable_ReturnsAllCategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Horror");
        category.setDescription("Scary");

        CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        Pageable pageable = PageRequest.of(0,10);
        List<Category> categories = List.of(category);
        Page<Category> categoriesPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoriesPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        List<CategoryDto> categoryDtoList = categoryService.findAll(pageable);

        // Then
        assertThat(categoryDtoList).hasSize(1);
        assertThat(categoryDtoList.get(0)).isEqualTo(categoryDto);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify getById() method works
            """)
    public void getById_ValidId_ReturnsCategoryDto() {
        // Given
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Horror");
        category.setDescription("Scary");

        CategoryDto expectedCategoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedCategoryDto);

        // When
        CategoryDto savedCategoryDto = categoryService.getById(categoryId);

        // Then
        assertThat(savedCategoryDto).isEqualTo(expectedCategoryDto);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);

    }

    @Test
    @DisplayName("""
            Verify deleteById() method works
            """)
    public void deleteById_ValidId_DeletesCategory() {
        // Given
        Long categoryId = 1L;

        // When
        categoryService.deleteById(categoryId);

        // Then
        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
