package com.ukpatel.expense.tracker.auth.repo;

import com.ukpatel.expense.tracker.auth.entity.BlacklistedJwtTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedJwtTxnRepo extends JpaRepository<BlacklistedJwtTxn, Long> {

}
