package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
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
    private BudgetItemUpdateDao budgetItemUpdateDao;

    public void createBudgetItem(BudgetItem budgetItem, String loggedInUserEmailAddress){
        Budget budget = budgetDao.findById(budgetItem.getBudget().getId()).get();
        User user = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        if(user.getId() != budget.getUser().getId()){
            throw new RuntimeException();
        }
        budgetItem.setBudget(budget);
        budgetItemDao.save(budgetItem);
    }

    public void deleteBudgetItem(long budgetItemId, String loggedInUserEmailAddress){
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        BudgetItem itemToDelete = budgetItemDao.getOne(budgetItemId);
        if(loggedInUser.getId() != itemToDelete.getBudget().getUser().getId()){
            throw new RuntimeException();
        }
        budgetItemDao.deleteById(itemToDelete.getId());
    }

    public BigDecimal getAmountSpent(long budgetItemId, int month, int year){

        BudgetItem budgetItem = budgetItemDao.getOne(budgetItemId);
        List<BudgetEntry> budgetEntries = budgetItem.getBudgetEntries();
        BigDecimal total = BigDecimal.ZERO;
        List<BudgetEntry> correspondingBudgetEntries;
        if(budgetItem.getBudgetItemType() == BudgetItemType.ANNUAL){
            correspondingBudgetEntries = budgetEntries.stream()
                    .filter(x -> x.getYear() == year)
                    .collect(Collectors.toList());
        }

        else{
            correspondingBudgetEntries = budgetEntries.stream()
                    .filter(x -> x.getYear() == year && x.getMonth().getMonthNumber() == month)
                    .collect(Collectors.toList());
        }
        for(BudgetEntry budgetEntry : correspondingBudgetEntries){
            BigDecimal entryAmount = budgetEntry.getAmount();
            total = total.add(entryAmount);
        }
        return total;
    }

    public BigDecimal getAmountRemaining(long budgetItemId, int month, int year, BigDecimal itemAmount){

        BigDecimal totalSpent = getAmountSpent(budgetItemId, month, year);
        BigDecimal amountRemaining = itemAmount.subtract(totalSpent);

        return amountRemaining;
    }

}
