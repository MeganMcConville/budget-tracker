package com.megansportfolio.budgettracker.budgetItemUpdate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetItemUpdateDao extends JpaRepository<BudgetItemUpdate, Long> {

    BudgetItemUpdate findOneByBudgetItemIdAndMonthAndYear(long budgetItemId, int month, int year);

}
