package com.megansportfolio.budgettracker.budgetItemHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetItemHistoryDao extends JpaRepository<BudgetItemHistory, Long> {
}
