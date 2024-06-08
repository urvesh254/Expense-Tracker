package com.ukpatel.expense.tracker.processes.cashbook.service;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.dto.CashbookDTO;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.repo.CashbookRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;

@Service
@RequiredArgsConstructor
public class CashbookService {

    private final CashbookRepo cashbookRepo;
    private final CashbookValidationService cashbookValidationService;

    @Transactional
    public CashbookDTO createCashbook(CashbookDTO cashbookDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        try {
            // Saving Cashbook
            Cashbook cashbook = new Cashbook();
            cashbook.setCashbookName(cashbookDTO.getCashbookName().trim());
            cashbook.setUserMst(loggedInUser);
            cashbook.setActiveFlag(STATUS_ACTIVE);
            cashbook.setCreatedByUser(loggedInUser);
            cashbook.setCreatedDate(new Date());
            cashbook.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
            cashbookRepo.save(cashbook);

            return CashbookDTO.builder()
                    .userId(loggedInUser.getUserId())
                    .cashbookId(cashbook.getCashbookId())
                    .cashbookName(cashbook.getCashbookName())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "CashbookName already exists");
        }
    }

    @Transactional
    public CashbookDTO updateCashbook(CashbookDTO cashbookDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        // Updating Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookDTO.getCashbookId());

        if (cashbookRepo.existsByCashbookName(cashbookDTO.getCashbookName().trim())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "CashbookName already exists");
        }

        cashbook.setCashbookName(cashbookDTO.getCashbookName().trim());
        cashbook.setUpdatedByUser(loggedInUser);
        cashbook.setUpdatedDate(new Date());
        cashbook.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        cashbookRepo.save(cashbook);

        cashbookDTO.setUserId(loggedInUser.getUserId());
        return cashbookDTO;
    }

    @Transactional
    public void deleteCashbook(Long cashbookId) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        // Soft deleting Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);
        cashbook.setActiveFlag(STATUS_INACTIVE);
        cashbook.setUpdatedByUser(loggedInUser);
        cashbook.setUpdatedDate(new Date());
        cashbook.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        cashbookRepo.save(cashbook);
    }

    public CashbookDTO getCashbookByCashbookId(Long cashbookId) {
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);
        if (cashbook.getActiveFlag().equals(STATUS_INACTIVE)) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Cashbook not found");
        }

        return CashbookDTO.builder()
                .userId(cashbook.getUserMst().getUserId())
                .cashbookId(cashbook.getCashbookId())
                .cashbookName(cashbook.getCashbookName())
                .build();
    }

    public List<CashbookDTO> getAllActiveCashbooks() {
        UserMst loggedInUser = getLoggedInUser();
        List<Cashbook> activeCashbooks = cashbookRepo.findByUserMstUserIdAndActiveFlag(loggedInUser.getUserId(), STATUS_ACTIVE);
        return activeCashbooks.stream()
                .map(cashbook -> CashbookDTO.builder()
                        .userId(cashbook.getUserMst().getUserId())
                        .cashbookId(cashbook.getCashbookId())
                        .cashbookName(cashbook.getCashbookName())
                        .build())
                .toList();
    }
}
