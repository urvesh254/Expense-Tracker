package com.ukpatel.expense.tracker.processes.category.service;

import com.ukpatel.expense.tracker.auth.entity.UserMst;
import com.ukpatel.expense.tracker.common.dto.UserSessionInfo;
import com.ukpatel.expense.tracker.exception.ApplicationException;
import com.ukpatel.expense.tracker.processes.cashbook.entity.Cashbook;
import com.ukpatel.expense.tracker.processes.cashbook.service.CashbookValidationService;
import com.ukpatel.expense.tracker.processes.category.dto.CategoryDTO;
import com.ukpatel.expense.tracker.processes.category.entity.Category;
import com.ukpatel.expense.tracker.processes.category.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.ukpatel.expense.tracker.common.constant.CmnConstants.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final CashbookValidationService cashbookValidationService;
    private final CategoryValidationService categoryValidationService;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        // Validating Cashbook
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(categoryDTO.getCashbookId());
        String categoryName = categoryDTO.getCategoryName().trim();

        // Validating Cashbook Category
        if (categoryRepo.existsByCashbookCashbookIdAndCategoryNameAndActiveFlag(categoryDTO.getCashbookId(), categoryName, STATUS_ACTIVE)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Category already exists");
        }

        // Saving Category Values
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCashbook(cashbook);
        category.setActiveFlag(STATUS_ACTIVE);
        category.setCreatedByUser(loggedInUser);
        category.setCreatedDate(new Date());
        category.setCreatedByIp(userSessionInfo.getRemoteIpAddr());
        categoryRepo.save(category);

        categoryDTO.setCategoryId(category.getCategoryId());
        return categoryDTO;
    }

    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        // Validating Cashbook Category
        String categoryName = categoryDTO.getCategoryName().trim();
        Category category = categoryValidationService.validateCashbookCategory(categoryDTO.getCashbookId(), categoryDTO.getCategoryId());
        if (categoryRepo.existsByCashbookCashbookIdAndCategoryNameAndActiveFlag(categoryDTO.getCashbookId(), categoryName, STATUS_ACTIVE)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "Category already exists");
        }

        // Updating Category Values
        category.setCategoryName(categoryName);
        category.setUpdatedByUser(loggedInUser);
        category.setUpdatedDate(new Date());
        category.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        categoryRepo.save(category);

        return categoryDTO;
    }

    @Transactional
    public void deleteCategory(Long cashbookId, Long categoryId) {
        UserSessionInfo userSessionInfo = getUserSessionInfo();
        UserMst loggedInUser = getLoggedInUser();

        // Validating Cashbook Category
        Category category = categoryValidationService.validateCashbookCategory(cashbookId, categoryId);
        category.setActiveFlag(STATUS_INACTIVE);
        category.setUpdatedByUser(loggedInUser);
        category.setUpdatedDate(new Date());
        category.setUpdatedByIp(userSessionInfo.getRemoteIpAddr());
        categoryRepo.save(category);
    }

    public CategoryDTO getCategoryByCategoryId(Long cashbookId, Long categoryId) {
        Category category = categoryValidationService.validateCashbookCategory(cashbookId, categoryId);
        return CategoryDTO.builder()
                .categoryId(categoryId)
                .cashbookId(cashbookId)
                .categoryName(category.getCategoryName())
                .build();
    }

    public List<CategoryDTO> getAllActiveCategories(Long cashbookId) {
        Cashbook cashbook = cashbookValidationService.validateCashbookUser(cashbookId);
        List<Category> activeCategories = categoryRepo.findByCashbookCashbookIdAndActiveFlag(cashbook.getCashbookId(), STATUS_ACTIVE);
        return activeCategories.stream()
                .map(category -> CategoryDTO.builder()
                        .categoryId(category.getCategoryId())
                        .cashbookId(cashbook.getCashbookId())
                        .categoryName(category.getCategoryName())
                        .build())
                .toList();
    }
}
