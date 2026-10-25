package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.Category;
import com.eventbooking.event_service.dto.request.CategoryRequest;
import com.eventbooking.event_service.exception.ResourceConflictException;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.mapper.CategoryMapper;
import com.eventbooking.event_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public Category createCategory(CategoryRequest request) {
        log.info("Creating category: {}", request.getName());
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ResourceConflictException("Category already exists with name: " + request.getName());
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        Category category = getCategoryById(id);

        categoryRepository.findByNameIgnoreCase(request.getName().trim())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResourceConflictException("Another category already exists with name: " + request.getName());
                });

        categoryMapper.updateEntity(category, request);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Deleting category with ID: {}", id);
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
