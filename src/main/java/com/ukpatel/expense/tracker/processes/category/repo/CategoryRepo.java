package com.ukpatel.expense.tracker.processes.category.repo;

import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    boolean existsByCashbookCashbookIdAndCategoryNameAndActiveFlag(Long cashbookId, String categoryName, Short activeFlag);

    Optional<Category> findByCategoryIdAndCashbookAndActiveFlag(Long categoryId, Cashbook cashbook, Short activeFlag);

    List<Category> findByCashbookCashbookIdAndActiveFlag(Long cashbookId, Short activeFlag);
}
