package com.ukpatel.expense.tracker.processes.cashbook.controller;

import com.ukpatel.expense.tracker.processes.cashbook.dto.CashbookDTO;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cashbooks")
@RequiredArgsConstructor
public class CashbookController {

    private final CashbookService cashbookService;

    @PostMapping
    public ResponseEntity<?> createCashbook(
            @Valid @RequestBody CashbookDTO cashbookDTO
    ) {
        cashbookDTO = cashbookService.createCashbook(cashbookDTO);
        return new ResponseEntity<>(cashbookDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{cashbookId}")
    public ResponseEntity<?> updateCashbook(
            @PathVariable Long cashbookId,
            @Valid @RequestBody CashbookDTO cashbookDTO
    ) {
        cashbookDTO.setCashbookId(cashbookId);
        cashbookDTO = cashbookService.updateCashbook(cashbookDTO);
        return ResponseEntity.ok(cashbookDTO);
    }

    @DeleteMapping("/{cashbookId}")
    public void deleteCashbook(
            @PathVariable Long cashbookId
    ) {
        cashbookService.deleteCashbook(cashbookId);
    }

    @GetMapping("/{cashbookId}")
    public ResponseEntity<?> getCashbookByCashbookId(
            @PathVariable Long cashbookId
    ) {
        CashbookDTO cashbookDTO = cashbookService.getCashbookByCashbookId(cashbookId);
        return ResponseEntity.ok(cashbookDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveCashbooks() {
        List<CashbookDTO> activeCashbooks = cashbookService.getAllActiveCashbooks();
        return ResponseEntity.ok(activeCashbooks);
    }
}

