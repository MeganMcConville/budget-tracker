package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public void updateBudgetItems(List<BudgetItem> budgetItems, String loggedInUserEmailAddress){
        List<Long> ids = budgetItems.stream().map(BudgetItem::getId).collect(Collectors.toList());
        List<BudgetItem> originalBudgetItems = budgetItemDao.findAllById(ids);
        for(BudgetItem budgetItem : budgetItems){
            BudgetItem originalBudgetItem = originalBudgetItems.stream().filter(x -> x.getId() == budgetItem.getId()).findFirst().get();

            if(!originalBudgetItem.getBudget().getUser().getUsername().equals(loggedInUserEmailAddress)){
                throw new RuntimeException();
            }
            if(budgetItem.getAmount() != null){
                originalBudgetItem.setAmount(budgetItem.getAmount());
            }
            if(budgetItem.getName() != null){
                originalBudgetItem.setName(budgetItem.getName());
            }
        }
    }

    public void deleteBudgetItem(long budgetItemId, String loggedInUserEmailAddress){
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        BudgetItem itemToDelete = budgetItemDao.getOne(budgetItemId);
        if(loggedInUser.getId() != itemToDelete.getBudget().getUser().getId()){
            throw new RuntimeException();
        }
        budgetItemDao.deleteById(itemToDelete.getId());
    }

}
