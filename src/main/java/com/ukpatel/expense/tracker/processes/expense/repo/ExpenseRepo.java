package com.ukpatel.expense.tracker.processes.expense.repo;

import com.ukpatel.expense.tracker.processes.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    Optional<Expense> findByExpenseIdAndCashbookCashbookIdAndActiveFlag(Long expenseId, Long cashbookId, Short activeFlag);

    List<Expense> findByCashbookCashbookIdAndActiveFlagOrderByEntryDateTimeDesc(Long cashbookId, Short activeFlag);

}
