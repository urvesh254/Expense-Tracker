package com.ukpatel.expense.tracker.processes.paymentMode.service;

import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookValidationService;
import com.ukpatel.expense.tracker.processes.paymentMode.dto.PaymentModeDTO;
import com.ukpatel.expense.tracker.processes.paymentMode.entity.PaymentMode;
import com.ukpatel.expense.tracker.processes.paymentMode.repo.PaymentModeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_ACTIVE;
import static com.ukpatel.expense.tracker.common.constant.CmnConstants.STATUS_INACTIVE;

@Service
@RequiredArgsConstructor
public class PaymentModeService {

    private final PaymentModeRepo paymentModeRepo;
    private final CashbookValidationService cashbookValidationService;
    private final PaymentModeValidationService paymentModeValidationService;

    @Transactional
    public PaymentModeDTO createPaymentMode(PaymentModeDTO paymentModeDTO) {
        // Validating Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(paymentModeDTO.getCashbookId());
        String paymentModeName = paymentModeDTO.getPaymentModeName().trim();

        // Validating Cashbook Category
        if (paymentModeRepo.existsByCashbookCashbookIdAndPaymentModeNameAndActiveFlag(
                paymentModeDTO.getCashbookId(),
                paymentModeName,
                STATUS_ACTIVE
        )) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Payment Mode already exists");
        }

        // Saving Payment Mode Values
        PaymentMode paymentMode = new PaymentMode();
        paymentMode.setPaymentModeName(paymentModeName);
        paymentMode.setCashbook(cashbook);
        paymentModeRepo.save(paymentMode);

        paymentModeDTO.setPaymentModeId(paymentMode.getPaymentModeId());
        return paymentModeDTO;
    }

    @Transactional
    public PaymentModeDTO updatePaymentMode(PaymentModeDTO paymentModeDTO) {
        // Validating Cashbook Payment Mode
        String paymentModeName = paymentModeDTO.getPaymentModeName().trim();
        PaymentMode paymentMode = paymentModeValidationService.validateCashbookPaymentMode(paymentModeDTO.getCashbookId(), paymentModeDTO.getPaymentModeId());
        if (paymentModeRepo.existsByCashbookCashbookIdAndPaymentModeNameAndActiveFlag(
                paymentModeDTO.getCashbookId(),
                paymentModeName,
                STATUS_ACTIVE
        )) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Payment Mode already exists");
        }

        // Updating Payment Mode Values
        paymentMode.setPaymentModeName(paymentModeName);
        paymentModeRepo.save(paymentMode);

        return paymentModeDTO;
    }

    @Transactional
    public void deletePaymentMode(Long cashbookId, Long paymentModeId) {
        // Validating Cashbook Payment Mode
        PaymentMode paymentMode = paymentModeValidationService.validateCashbookPaymentMode(cashbookId, paymentModeId);
        paymentMode.setActiveFlag(STATUS_INACTIVE);
        paymentModeRepo.save(paymentMode);
    }

    public PaymentModeDTO getPaymentModeByPaymentModeId(Long cashbookId, Long paymentModeId) {
        PaymentMode paymentMode = paymentModeValidationService.validateCashbookPaymentMode(cashbookId, paymentModeId);
        return PaymentModeDTO.builder()
                .paymentModeId(paymentModeId)
                .cashbookId(cashbookId)
                .paymentModeName(paymentMode.getPaymentModeName())
                .build();
    }

    public List<PaymentModeDTO> getAllActivePaymentModes(Long cashbookId) {
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);
        List<PaymentMode> activePaymentModes = paymentModeRepo.findByCashbookCashbookIdAndActiveFlag(cashbook.getCashbookId(), STATUS_ACTIVE);
        return activePaymentModes.stream()
                .map(paymentMode -> PaymentModeDTO.builder()
                        .paymentModeId(paymentMode.getPaymentModeId())
                        .cashbookId(cashbookId)
                        .paymentModeName(paymentMode.getPaymentModeName())
                        .build())
                .toList();
    }
}
