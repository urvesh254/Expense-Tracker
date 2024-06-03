package com.ukpatel.expense.tracker.auth.repo;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMstRepo extends JpaRepository<UserMst, Long> {

    Optional<UserMst> findByEmail(String username);
}
