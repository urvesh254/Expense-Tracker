package com.ukpatel.expense.tracker.processes.category.controller;

import com.ukpatel.expense.tracker.processes.category.dto.CategoryDTO;
import com.ukpatel.expense.tracker.processes.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cashbooks/{cashbookId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(
            @PathVariable Long cashbookId,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryDTO.setCashbookId(cashbookId);
        categoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(categoryDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long cashbookId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryDTO.setCashbookId(cashbookId);
        categoryDTO.setCategoryId(categoryId);
        categoryDTO = categoryService.updateCategory(categoryDTO);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @PathVariable Long cashbookId,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(cashbookId, categoryId);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryByCategoryId(
            @PathVariable Long cashbookId,
            @PathVariable Long categoryId
    ) {
        CategoryDTO categoryDTO = categoryService.getCategoryByCategoryId(cashbookId, categoryId);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveCategories(
            @PathVariable Long cashbookId
    ) {
        List<CategoryDTO> activeCategories = categoryService.getAllActiveCategories(cashbookId);
        return ResponseEntity.ok(activeCategories);
    }

}

