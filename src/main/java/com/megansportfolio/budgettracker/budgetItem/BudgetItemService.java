package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetItemService {

    @Autowired
    private BudgetItemDao budgetItemDao;

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private UserDao userDao;

    public void createBudgetItem(BudgetItem budgetItem, String loggedInUserEmailAddress){
        Budget budget = budgetDao.findById(budgetItem.getBudget().getId()).get();
        User user = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        if(user.getId() != budget.getUser().getId()){
            throw new RuntimeException();
        }
        budgetItem.setBudget(budget);
        budgetItemDao.save(budgetItem);
    }

}
