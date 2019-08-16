package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetEntryService {

    @Autowired
    BudgetEntryDao budgetEntryDao;

    @Autowired
    BudgetItemDao budgetItemDao;

    @Autowired
    UserDao userDao;

    public void createBudgetEntry(String loggedInUserEmailAddress, BudgetEntry budgetEntry){
        BudgetItem budgetItem = budgetItemDao.getOne(budgetEntry.getBudgetItem().getId());
        User user = budgetItem.getBudget().getUser();
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        if(user.getId() != loggedInUser.getId()){
            throw new RuntimeException();
        }
        Month month = Month.valueOfMonthNumber(budgetEntry.getMonthNumber());
        budgetEntry.setBudgetEntryMonth(month);
        budgetEntryDao.save(budgetEntry);
    }

}
