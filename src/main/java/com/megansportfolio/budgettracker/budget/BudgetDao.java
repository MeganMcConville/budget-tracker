package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetDao extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

}
