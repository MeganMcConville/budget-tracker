package com.megansportfolio.budgettracker.budgetEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetEntryDao extends JpaRepository<BudgetEntry, Long> {
}
