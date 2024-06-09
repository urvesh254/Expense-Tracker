package com.ukpatel.expense.tracker.processes.expense.controller;

import com.ukpatel.expense.tracker.processes.expense.dto.PaymentModeDTO;
import com.ukpatel.expense.tracker.processes.expense.service.PaymentModeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cashbooks/{cashbookId}/payment-modes")
@RequiredArgsConstructor
public class PaymentModeController {

    private final PaymentModeService paymentModeService;

    @PostMapping
    public ResponseEntity<?> createPaymentMode(
            @PathVariable Long cashbookId,
            @Valid @RequestBody PaymentModeDTO paymentModeDTO
    ) {
        paymentModeDTO.setCashbookId(cashbookId);
        paymentModeDTO = paymentModeService.createPaymentMode(paymentModeDTO);
        return new ResponseEntity<>(paymentModeDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{paymentModeId}")
    public ResponseEntity<?> updatePaymentMode(
            @PathVariable Long cashbookId,
            @PathVariable Long paymentModeId,
            @Valid @RequestBody PaymentModeDTO paymentModeDTO
    ) {
        paymentModeDTO.setPaymentModeId(paymentModeId);
        paymentModeDTO.setCashbookId(cashbookId);
        paymentModeDTO = paymentModeService.updatePaymentMode(paymentModeDTO);
        return new ResponseEntity<>(paymentModeDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{paymentModeId}")
    public void deletePaymentMode(
            @PathVariable Long cashbookId,
            @PathVariable Long paymentModeId
    ) {
        paymentModeService.deletePaymentMode(cashbookId, paymentModeId);
    }

    @GetMapping("/{paymentModeId}")
    public ResponseEntity<?> getPaymentModeByPaymentModeId(
            @PathVariable Long cashbookId,
            @PathVariable Long paymentModeId
    ) {
        PaymentModeDTO paymentModeDTO = paymentModeService.getPaymentModeByPaymentModeId(cashbookId, paymentModeId);
        return ResponseEntity.ok(paymentModeDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllActivePaymentModes(
            @PathVariable Long cashbookId
    ) {
        List<PaymentModeDTO> activePaymentModes = paymentModeService.getAllActivePaymentModes(cashbookId);
        return ResponseEntity.ok(activePaymentModes);
    }
}
