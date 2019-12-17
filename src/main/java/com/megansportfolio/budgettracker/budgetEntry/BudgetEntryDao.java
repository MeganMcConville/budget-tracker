package com.megansportfolio.budgettracker.budgetEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetEntryDao extends JpaRepository<BudgetEntry, Long> {

    List<BudgetEntry> findAllByBudgetItemIdAndYear(long budgetItemId, int year);

}
