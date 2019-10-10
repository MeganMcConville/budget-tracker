package com.megansportfolio.budgettracker.sharedUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedUserDao extends JpaRepository<SharedUser, Long> {

    SharedUser findOneByEmailAndBudgetId(String email, long budgetId);

}
