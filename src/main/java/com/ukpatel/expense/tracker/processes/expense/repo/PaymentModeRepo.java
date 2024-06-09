package com.ukpatel.expense.tracker.processes.expense.repo;

import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.expense.entity.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentModeRepo extends JpaRepository<PaymentMode, Long> {

    boolean existsByCashbookCashbookIdAndPaymentModeNameAndActiveFlag(Long cashbookId, String paymentModeName, Short activeFlag);

    Optional<PaymentMode> findByPaymentModeIdAndCashbookAndActiveFlag(Long paymentModeId, Cashbook cashbook, Short activeFlag);

    List<PaymentMode> findByCashbookCashbookIdAndActiveFlag(Long cashbookId, Short activeFlag);
}
