package com.ukpatel.expense.tracker.processes.expense.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.processes.expense.constant.EntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ExpenseDTO {

    private Long expenseId;

    @NotNull(message = "entryType is required and must be either CASH_IN or CASH_OUT")
    private EntryType entryType;

    @NotNull(message = "entryDateTime is required and cannot be null")
    private Date entryDateTime;

    @NotNull(message = "amount is required and cannot be blank")
    @DecimalMin(value = "0.0", message = "Value of amount must be greater than 0.0", inclusive = false)
    private Double amount;

    private String remarks;
    private Long categoryId;
    private Long paymentModeId;
    private Long cashbookId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AttachmentDTO attachment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long attachmentId;

    @JsonIgnore
    private boolean isValidAttachment;
}
