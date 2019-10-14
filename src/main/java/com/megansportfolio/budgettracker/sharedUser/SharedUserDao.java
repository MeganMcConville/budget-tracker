package com.megansportfolio.budgettracker.sharedUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedUserDao extends JpaRepository<SharedUser, Long> {

    SharedUser findOneByEmailIgnoreCaseAndBudgetId(String email, long budgetId);

    List<SharedUser> findAllByEmailIgnoreCase(String email);

}
