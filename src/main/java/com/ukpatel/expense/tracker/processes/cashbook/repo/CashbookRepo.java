package com.ukpatel.expense.tracker.processes.cashbook.repo;

import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashbookRepo extends JpaRepository<Cashbook, Long> {

    List<Cashbook> findByUserMstUserIdAndActiveFlag(Long userId, Short activeFlag);

    Optional<Cashbook> findByCashbookIdAndActiveFlag(Long cashbookId, Short activeFlag);

    boolean existsByCashbookName(String cashbookName);
}
