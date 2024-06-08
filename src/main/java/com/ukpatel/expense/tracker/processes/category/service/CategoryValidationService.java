package com.ukpatel.expense.tracker.processes.category.service;

import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookValidationService;
import com.ukpatel.expense.tracker.processes.category.entity.Category;
import com.ukpatel.expense.tracker.processes.category.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class CategoryValidationService {

    private final CashbookValidationService cashbookValidationService;
    private final CategoryRepo categoryRepo;

    public Category validateCashbookCategory(Long cashbookId, Long categoryId) {
        if (categoryId == null) {
            throw new ApplicationException("categoryId is required");
        }
        // Validate Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);

        // Validate Cashbook and Category with activeFlag
        return categoryRepo.findByCategoryIdAndCashbookAndActiveFlag(categoryId, cashbook, STATUS_ACTIVE)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Category not found"));
    }
}
