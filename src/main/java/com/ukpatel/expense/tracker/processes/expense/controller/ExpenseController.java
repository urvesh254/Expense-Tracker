package com.ukpatel.expense.tracker.processes.expense.controller;

import com.ukpatel.expense.tracker.processes.expense.dto.ExpenseDTO;
import com.ukpatel.expense.tracker.processes.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cashbooks/{cashbookId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> createExpense(
            @PathVariable Long cashbookId,
            @Valid @RequestBody ExpenseDTO expenseDTO
    ) {
        expenseDTO.setCashbookId(cashbookId);
        expenseDTO = expenseService.createExpense(expenseDTO);
        return new ResponseEntity<>(expenseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<?> createExpense(
            @PathVariable Long cashbookId,
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseDTO expenseDTO
    ) {
        expenseDTO.setCashbookId(cashbookId);
        expenseDTO.setExpenseId(expenseId);
        expenseDTO = expenseService.updateExpense(expenseDTO);
        return ResponseEntity.ok(expenseDTO);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(
            @PathVariable Long cashbookId,
            @PathVariable Long expenseId
    ) {
        expenseService.deleteExpense(cashbookId, expenseId);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<?> getExpenseByExpenseId(
            @PathVariable Long cashbookId,
            @PathVariable Long expenseId
    ) {
        ExpenseDTO expenseDTO = expenseService.getActiveExpenseByExpenseId(cashbookId, expenseId);
        return ResponseEntity.ok(expenseDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveExpense(
            @PathVariable Long cashbookId
    ) {
        List<ExpenseDTO> activeExpenses = expenseService.getAllActiveExpense(cashbookId);
        return ResponseEntity.ok(activeExpenses);
    }
}
