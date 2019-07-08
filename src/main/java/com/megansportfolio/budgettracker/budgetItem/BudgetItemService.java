package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budgetItemHistory.BudgetItemHistory;
import com.megansportfolio.budgettracker.budgetItemHistory.BudgetItemHistoryDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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

    @Autowired
    private BudgetItemHistoryDao budgetItemHistoryDao;

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
        List<BudgetItemHistory> budgetItemHistories = new ArrayList<>();
        for(BudgetItem budgetItem : budgetItems){
            BudgetItem originalBudgetItem = originalBudgetItems.stream().filter(x -> x.getId() == budgetItem.getId()).findFirst().get();
            boolean hasBeenUpdated = false;
            String originalName = originalBudgetItem.getName();
            BigDecimal originalAmount = originalBudgetItem.getAmount();

            if(!originalBudgetItem.getBudget().getUser().getUsername().equals(loggedInUserEmailAddress)){
                throw new RuntimeException();
            }
            if(budgetItem.getAmount() != null){
                originalBudgetItem.setAmount(budgetItem.getAmount());
                hasBeenUpdated = true;
            }
            if(budgetItem.getName() != null){
                originalBudgetItem.setName(budgetItem.getName());
                hasBeenUpdated = true;
            }
            if(hasBeenUpdated){
                BudgetItemHistory newBudgetItemHistory = new BudgetItemHistory();
                newBudgetItemHistory.setAmount(originalAmount);
                newBudgetItemHistory.setName(originalName);
                newBudgetItemHistory.setBudgetItem(originalBudgetItem);
                newBudgetItemHistory.setEditDate(new Date(Calendar.getInstance().getTime().getTime()));
                budgetItemHistories.add(newBudgetItemHistory);
            }

        }
        budgetItemHistoryDao.saveAll(budgetItemHistories);
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
