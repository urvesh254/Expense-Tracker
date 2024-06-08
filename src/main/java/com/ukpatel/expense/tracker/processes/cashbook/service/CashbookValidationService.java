package com.ukpatel.expense.tracker.processes.cashbook.service;

import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.repo.CashbookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.getUserSessionInfo;

@Service
@RequiredArgsConstructor
public class CashbookValidationService {

    private final CashbookRepo cashbookRepo;

    public Cashbook validateCashbookUser(Cashbook cashbook) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        if (!cashbook.getUserMst().getUserId().equals(userSessionInfo.getUserDTO().getUserId())) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Cashbook not found for loggedIn user");
        }
        return cashbook;
    }

    public Cashbook validateCashbookUser(Long cashbookId) {
        if (cashbookId == null) {
            throw new ApplicationException("cashbookId is required");
        }
        Cashbook cashbook = cashbookRepo.findById(cashbookId)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Cashbook not found"));
        return validateCashbookUser(cashbook);
    }

}
