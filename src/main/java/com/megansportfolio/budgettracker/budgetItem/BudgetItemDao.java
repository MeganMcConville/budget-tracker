package com.megansportfolio.budgettracker.budgetItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemDao extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findAllByBudgetItemType(BudgetItemType budgetItemType);

}
