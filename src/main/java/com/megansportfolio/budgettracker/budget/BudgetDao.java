package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BudgetDao extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    @Query(nativeQuery = true, value = "SELECT DISTINCT year FROM budget_entry WHERE budget_item_id IN (:budgetItemIds)")
    List<BigDecimal> findActiveYearsByBudgetItemId(@Param("budgetItemIds") List<Long> budgetItemIds);

}
