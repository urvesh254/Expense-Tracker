package com.ukpatel.expense.tracker.processes.paymentMode.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentModeDTO {

    private Long paymentModeId;
    private Long cashbookId;

    @NotBlank(message = "paymentModeName is required and cannot be blank")
    private String paymentModeName;
}
