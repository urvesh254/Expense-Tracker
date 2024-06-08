package com.ukpatel.expense.tracker.processes.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    private Long categoryId;
    private Long cashbookId;

    @NotBlank(message = "categoryName is required and cannot be blank")
    private String categoryName;
}
