package mate.academy.springbootintro.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.springbootintro.config.CustomMySqlContainer;
import mate.academy.springbootintro.dto.CategoryDto;
import mate.academy.springbootintro.dto.CreateCategoryRequestDto;
import mate.academy.springbootintro.model.Category;
import mate.academy.springbootintro.repository.category.CategoryRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        CustomMySqlContainer mysqlContainer = CustomMySqlContainer.getInstance();
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName);
    }

    @BeforeAll
    static void beforeAll() {
        CustomMySqlContainer.getInstance().start();
    }

    @AfterAll
    static void afterAll() {
        CustomMySqlContainer.getInstance().stop();
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Create a new category
            """)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequest_CreatesNewCategory() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Horor",
                "Scary"
        );

        CategoryDto expected = new CategoryDto(
                1L,
                requestDto.name(),
                requestDto.description()
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper.readValue
                (result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(actual, expected);
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get all categories
            """)
    @Sql(scripts = "classpath:database/categories/add-2-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_Categories_ReturnsAllCategories() throws Exception {
        // Given
        CategoryDto category1 = new CategoryDto(
                1L,
                "Fiction",
                "Fiction books"
        );

        CategoryDto category2 = new CategoryDto(
                2L,
                "Fantasy",
                "Fantasy books"
        );

        List<CategoryDto> expected = new ArrayList<>();
        expected.add(category1);
        expected.add(category2);

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get category by ID
            """)
    @Sql(scripts = "classpath:database/categories/add-2-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        // Given
        Long categoryId = 1L;

        CategoryDto expected = new CategoryDto(
                1L,
                "Fiction",
                "Fiction books"
        );

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        EqualsBuilder.reflectionEquals(actual, expected, "id");
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"ADMIN"})
    @DisplayName("""
            Delete category by ID
            """)
    @Sql(scripts = "classpath:database/categories/add-2-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_ValidId_DeletesCategory() throws Exception {
        // Given
        categoryRepository.findById(1L);

        // When
        mockMvc.perform(
                        delete("/api/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        Optional<Category> deletedCategory = categoryRepository.findById(1L);
        Assertions.assertFalse(deletedCategory.isPresent());
    }
}
