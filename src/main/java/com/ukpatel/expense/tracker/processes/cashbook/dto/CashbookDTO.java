package com.ukpatel.expense.tracker.processes.cashbook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CashbookDTO {

    private Long cashbookId;

    @NotBlank(message = "cashbookName is required and cannot be blank")
    private String cashbookName;
    private Long userId;
}
