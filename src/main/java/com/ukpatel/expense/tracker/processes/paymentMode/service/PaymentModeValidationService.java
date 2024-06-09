package com.ukpatel.expense.tracker.processes.paymentMode.service;

import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookValidationService;
import com.ukpatel.expense.tracker.processes.paymentMode.entity.PaymentMode;
import com.ukpatel.expense.tracker.processes.paymentMode.repo.PaymentModeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;

@Service
@RequiredArgsConstructor
public class PaymentModeValidationService {

    private final PaymentModeRepo paymentModeRepo;
    private final CashbookValidationService cashbookValidationService;

    public PaymentMode validateCashbookPaymentMode(Long cashbookId, Long paymentModeId) {
        if (paymentModeId == null) {
            throw new ApplicationException("paymentModeId is required");
        }
        // Validate Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);

        // Validate Cashbook and Payment Mode with activeFlag
        return paymentModeRepo.findByPaymentModeIdAndCashbookAndActiveFlag(paymentModeId, cashbook, STATUS_ACTIVE)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Payment Mode not found"));
    }
}
