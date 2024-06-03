package com.ukpatel.expense.tracker.auth.repo;

import com.ukpatel.expense.tracker.auth.entity.UserDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDtlRepo extends JpaRepository<UserDtl, Long> {

}
