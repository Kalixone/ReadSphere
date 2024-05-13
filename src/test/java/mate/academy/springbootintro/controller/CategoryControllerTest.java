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
    private static final Long CATEGORY_ID_1 = 1L;
    private static final Long CATEGORY_ID_2 = 2L;
    private static final String SPRING_DATASOURCE_URL =
            "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME =
            "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD =
            "spring.datasource.password";
    private static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME =
            "spring.datasource.driver-class-name";
    private static final String CATEGORY_NAME_1 = "Fiction";
    private static final String CATEGORY_DESCRIPTION_1 = "Fiction books";
    private static final String CATEGORY_NAME_2 = "Fantasy";
    private static final String CATEGORY_DESCRIPTION_2 = "Fantasy books";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        CustomMySqlContainer mysqlContainer = CustomMySqlContainer.getInstance();
        registry.add(SPRING_DATASOURCE_URL, mysqlContainer::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_USERNAME, mysqlContainer::getUsername);
        registry.add(SPRING_DATASOURCE_PASSWORD, mysqlContainer::getPassword);
        registry.add(SPRING_DATASOURCE_DRIVER_CLASS_NAME, mysqlContainer::getDriverClassName);
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
                CATEGORY_NAME_1,
                CATEGORY_DESCRIPTION_1
        );

        CategoryDto expected = createCategoryDto(
                CATEGORY_ID_1, requestDto.name(), requestDto.description());

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
    @Sql(scripts = {
            "classpath:database/categories/delete-categories-from-categories-table.sql",
            "classpath:database/categories/add-2-categories-to-categories-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAll_Categories_ReturnsAllCategories() throws Exception {
        // Given
        CategoryDto category1 = createCategoryDto(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        CategoryDto category2 = createCategoryDto(
                CATEGORY_ID_2, CATEGORY_NAME_2, CATEGORY_DESCRIPTION_2);

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
        Assertions.assertIterableEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "piotrek", authorities = {"USER"})
    @DisplayName("""
            Get category by ID
            """)
    @Sql(scripts = {
            "classpath:database/categories/delete-categories-from-categories-table.sql",
            "classpath:database/categories/add-2-categories-to-categories-table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/categories/delete-categories-from-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        // Given
        CategoryDto expected = createCategoryDto(
                CATEGORY_ID_1, CATEGORY_NAME_1, CATEGORY_DESCRIPTION_1);

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/categories/{id}", CATEGORY_ID_1)
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
        categoryRepository.findById(CATEGORY_ID_1);

        // When
        mockMvc.perform(
                        delete("/api/categories/{id}", CATEGORY_ID_1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        Optional<Category> deletedCategory = categoryRepository.findById(CATEGORY_ID_1);
        Assertions.assertFalse(deletedCategory.isPresent());
    }

    private CategoryDto createCategoryDto(
            Long id, String name,
            String description) {
        return new CategoryDto(id, name, description);
    }
}
