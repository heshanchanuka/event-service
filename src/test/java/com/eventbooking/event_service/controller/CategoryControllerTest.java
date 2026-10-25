package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.Category;
import com.eventbooking.event_service.dto.request.CategoryRequest;
import com.eventbooking.event_service.dto.response.CategoryResponse;
import com.eventbooking.event_service.exception.GlobalExceptionHandler;
import com.eventbooking.event_service.mapper.CategoryMapper;
import com.eventbooking.event_service.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    private UUID categoryId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        categoryId = UUID.randomUUID();
    }

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        String requestJson = """
                {
                    "name": "Technology",
                    "description": "Tech conferences"
                }
                """;

        Category category = Category.builder().id(categoryId).name("Technology").build();
        CategoryResponse response = CategoryResponse.builder().id(categoryId).name("Technology").build();

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    void shouldDeleteCategorySuccessfully() throws Exception {
        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNoContent());
    }
}
