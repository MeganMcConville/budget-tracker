package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Budget> findBudgets(String emailAddress){
        User currentUser = userDao.findOneByUsernameIgnoreCase(emailAddress);
        List<Budget> userBudgets = budgetDao.findByUser(currentUser);
        return userBudgets;
    }

    public Budget getBudget(String loggedInUserEmailAddress, long budgetId){
        User currentUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        Budget budget = budgetDao.getOne(budgetId);
        if(currentUser.getId() == budget.getUser().getId()){
            return budget;
        }
        throw new RuntimeException();
    }

    public void renameBudget(Budget budget, String loggedInUserEmailaddress){
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailaddress);
        Budget existingBudget = budgetDao.getOne(budget.getId());
        if(loggedInUser.getId() != existingBudget.getUser().getId()){
            throw new RuntimeException();
        }
        if(budget.getName() != null){
            String updatedName = budget.getName().trim();
            //check if if empty string
            if(updatedName.length() < 1){
                throw new RuntimeException();
            }
            existingBudget.setName(updatedName);
        }
        budgetDao.save(existingBudget);
    }

}
