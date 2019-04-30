package com.megansportfolio.budgettracker.budgetItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemDao extends JpaRepository<BudgetItem, Long> {
}
