package com.megansportfolio.budgettracker.budgetItemUpdate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetItemUpdateDao extends JpaRepository<BudgetItemUpdate, Long> {

    BudgetItemUpdate findOneByBudgetItemIdAndMonthAndYear(long budgetItemId, int month, int year);

    List<BudgetItemUpdate> findAllByBudgetItemId(long budgetItemId);

}
