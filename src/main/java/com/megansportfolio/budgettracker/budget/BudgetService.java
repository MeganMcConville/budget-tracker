package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private UserDao userDao;

    public long createBudget(Budget budget, String loggedInUserEmailAddress){

        User user = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        budget.setUser(user);
        return budgetDao.save(budget).getId();

    }
}
