package com.ukpatel.expense.tracker.processes.expense.service;

import com.ukpatel.expense.tracker.attachment.dto.AttachmentDTO;
import com.ukpatel.expense.tracker.attachment.service.AttachmentService;
import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.expense.dto.ExpenseDTO;
import com.ukpatel.expense.tracker.processes.expense.entity.Expense;
import com.ukpatel.expense.tracker.processes.expense.repo.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;
import static com.ukpatel.expense.tracker.processes.expense.service.ExpenseValidationService.VALIDATION_TYPE_CREATE;
import static com.ukpatel.expense.tracker.processes.expense.service.ExpenseValidationService.VALIDATION_TYPE_UPDATE;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private static final Function<Expense, ExpenseDTO> expenseEToD = expense -> ExpenseDTO.builder()
            .expenseId(expense.getExpenseId())
            .cashbookId(expense.getCashbook().getCashbookId())
            .entryType(expense.getEntryType())
            .entryDateTime(expense.getEntryDateTime())
            .amount(expense.getAmount())
            .remarks(expense.getRemarks())
            .categoryId(expense.getCategory() != null ? expense.getCategory().getCategoryId() : null)
            .paymentModeId(expense.getPaymentMode() != null ? expense.getPaymentMode().getPaymentModeId() : null)
            .attachmentId(expense.getAttachmentMst() != null ? expense.getAttachmentMst().getAttachmentId() : null)
            .build();
    private final ExpenseValidationService expenseValidationService;
    private final AttachmentService attachmentService;
    private final ExpenseRepo expenseRepo;

    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        Expense expense = expenseValidationService.validateExpenseDTO(expenseDTO, VALIDATION_TYPE_CREATE);

        // Saving Attachment
        if (expenseDTO.isValidAttachment()) {
            AttachmentDTO attachment = expenseDTO.getAttachment();
            attachmentService.saveFileAttachments(attachment);
            attachmentService.findById(attachment.getAttachmentId())
                    .ifPresent(expense::setAttachmentMst);
        }

        expense.setActiveFlag(STATUS_ACTIVE);
        expense.setCreatedByUser(loggedInUser);
        expense.setCreatedDate(new Date());
        expense.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        expenseRepo.save(expense);

        Long attachmentId = expense.getAttachmentMst() != null
                ? expense.getAttachmentMst().getAttachmentId()
                : null;
        expenseDTO.setAttachmentId(attachmentId);
        expenseDTO.setExpenseId(expense.getExpenseId());
        return expenseDTO;
    }

    @Transactional
    public ExpenseDTO updateExpense(ExpenseDTO expenseDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        Expense expense = expenseValidationService.validateExpenseDTO(expenseDTO, VALIDATION_TYPE_UPDATE);

        // Saving Attachment
        if (expenseDTO.isValidAttachment()) {
            AttachmentDTO attachment = expenseDTO.getAttachment();
            attachmentService.saveFileAttachments(attachment);
            attachmentService.findById(attachment.getAttachmentId())
                    .ifPresent(expense::setAttachmentMst);
        }

        expense.setUpdatedByUser(loggedInUser);
        expense.setUpdatedDate(new Date());
        expense.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        expenseRepo.save(expense);

        Long attachmentId = expense.getAttachmentMst() != null
                ? expense.getAttachmentMst().getAttachmentId()
                : null;
        expenseDTO.setAttachmentId(attachmentId);
        expenseDTO.setExpenseId(expense.getExpenseId());
        return expenseDTO;
    }

    @Transactional
    public void deleteExpense(Long cashbookId, Long expenseId) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        Expense expense = expenseRepo.findByExpenseIdAndCashbookCashbookIdAndActiveFlag(
                        expenseId,
                        cashbookId,
                        STATUS_ACTIVE
                )
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Expense not found"));

        expense.setActiveFlag(STATUS_INACTIVE);
        expense.setUpdatedByUser(loggedInUser);
        expense.setUpdatedDate(new Date());
        expense.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        expenseRepo.save(expense);
    }

    public ExpenseDTO getActiveExpenseByExpenseId(Long cashbookId, Long expenseId) {
        Expense expense = expenseRepo.findByExpenseIdAndCashbookCashbookIdAndActiveFlag(
                        expenseId,
                        cashbookId,
                        STATUS_ACTIVE
                )
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Expense not found"));

        return expenseEToD.apply(expense);
    }

    public List<ExpenseDTO> getAllActiveExpense(Long cashbookId) {
        List<Expense> activeExpenses = expenseRepo.findByCashbookCashbookIdAndActiveFlagOrderByEntryDateTimeDesc(cashbookId, STATUS_ACTIVE);
        return activeExpenses.stream()
                .map(expenseEToD)
                .toList();
    }
}
