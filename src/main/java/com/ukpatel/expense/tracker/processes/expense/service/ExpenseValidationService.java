package com.ukpatel.expense.tracker.processes.expense.service;

import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookValidationService;
import com.ukpatel.expense.tracker.processes.category.entity.Category;
import com.ukpatel.expense.tracker.processes.category.service.CategoryValidationService;
import com.ukpatel.expense.tracker.processes.expense.dto.ExpenseDTO;
import com.ukpatel.expense.tracker.processes.expense.entity.Expense;
import com.ukpatel.expense.tracker.processes.expense.repo.ExpenseRepo;
import com.ukpatel.expense.tracker.processes.paymentMode.entity.PaymentMode;
import com.ukpatel.expense.tracker.processes.paymentMode.service.PaymentModeValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class ExpenseValidationService {

    public static final String VALIDATION_TYPE_CREATE = "VALIDATION_TYPE_CREATE";
    public static final String VALIDATION_TYPE_UPDATE = "VALIDATION_TYPE_UPDATE";

    private final CashbookValidationService cashbookValidationService;
    private final CategoryValidationService categoryValidationService;
    private final PaymentModeValidationService paymentModeValidationService;
    private final ExpenseRepo expenseRepo;

    public Expense validateExpenseDTO(ExpenseDTO expenseDTO, String validationType) {
        boolean isValidationTypeCreate = VALIDATION_TYPE_CREATE.equals(validationType);
        boolean isValidationTypeUpdate = VALIDATION_TYPE_UPDATE.equals(validationType);
        if (!(isValidationTypeCreate || isValidationTypeUpdate)) {
            throw new ApplicationException("Validate type must be from 'VALIDATION_TYPE_CREATE' or 'VALIDATION_TYPE_UPDATE' ");
        }

        Expense expense = new Expense();
        if (isValidationTypeUpdate) {
            expense = expenseRepo.findByExpenseIdAndCashbookCashbookIdAndActiveFlag(
                    expenseDTO.getExpenseId(),
                    expenseDTO.getCashbookId(),
                    STATUS_ACTIVE
            )
                    .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Expense not found"));
        }

        // Validating Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(expenseDTO.getCashbookId());
        expense.setCashbook(cashbook);

        // Validating Category
        if (expenseDTO.getCategoryId() != null) {
            Category category = categoryValidationService.validateCashbookCategory(expenseDTO.getCashbookId(), expenseDTO.getCategoryId());
            expense.setCategory(category);
        }

        // Validating Payment Mode
        if (expenseDTO.getPaymentModeId() != null) {
            PaymentMode paymentMode = paymentModeValidationService.validateCashbookPaymentMode(expenseDTO.getCashbookId(), expenseDTO.getPaymentModeId());
            expense.setPaymentMode(paymentMode);
        }

        if (isValidationTypeCreate || expenseDTO.getEntryType() != null) {
            expense.setEntryType(expenseDTO.getEntryType());
        }

        if (isValidationTypeCreate || expenseDTO.getEntryDateTime() != null) {
            expense.setEntryDateTime(expenseDTO.getEntryDateTime());
        }

        if (isValidationTypeCreate || expenseDTO.getAmount() != null) {
            expense.setAmount(expenseDTO.getAmount());
        }

        if (isValidationTypeCreate || expenseDTO.getRemarks() != null) {
            expense.setRemarks(expenseDTO.getRemarks());
        }

        // Validating Attachment
        AttachmentDTO attachment = expenseDTO.getAttachment();
        if (attachment != null
                && attachment.getAttachmentId() != null
                && attachment.getSessionId() != null) {
            expenseDTO.setValidAttachment(true);
        }
        return expense;
    }
}
