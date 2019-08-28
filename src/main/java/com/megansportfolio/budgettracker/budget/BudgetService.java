package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemService;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BudgetItemUpdateDao budgetItemUpdateDao;

    @Autowired
    private BudgetItemService budgetItemService;

    public int getDisplayYear(Integer year){
        int displayYear;
        if(year == null){
            Calendar cal = Calendar.getInstance();
            displayYear = (cal.get(Calendar.YEAR));
        }
        else{
            displayYear = year;
        }
        return displayYear;
    }

    public Month getDisplayMonth(Integer month){
        Month displayMonth;
        if(month == null){
            Calendar cal = Calendar.getInstance();
            month = (cal.get(Calendar.MONTH)) + 1;
        }
        displayMonth = Month.valueOfMonthNumber(month);

        return displayMonth;
    }

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

    @Transactional(readOnly = true)
    public Budget getBudget(String loggedInUserEmailAddress, long budgetId, Integer month, Integer year){
        User currentUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        Budget budget = budgetDao.getOne(budgetId);
        if(currentUser.getId() == budget.getUser().getId()){
            if(month == null || year == null){
                Calendar cal = Calendar.getInstance();
                month = (cal.get(Calendar.MONTH)) + 1;
                year = (cal.get(Calendar.YEAR));
            }

            LocalDate cutOff = LocalDate.of(year, month, 1);

            List<BudgetItem> budgetItems = budget.getBudgetItems();
            final int finalMonth = month;
            final int finalYear = year;
            for(BudgetItem budgetItem : budgetItems){
                BigDecimal displayAmount = budgetItem.getAmount();
                List<BudgetItemUpdate> budgetItemUpdates = budgetItemUpdateDao.findAllByBudgetItemId(budgetItem.getId());
                List<BudgetItemUpdate> budgetItemUpdatesBeforeCutoff = budgetItemUpdates.stream()
                        .filter(x ->  x.getDate().equals(cutOff) || x.getDate().isBefore(cutOff))
                        .sorted(Comparator.comparing(BudgetItemUpdate::getDate).reversed())
                        .collect(Collectors.toList());
                Optional<BudgetItemUpdate> correspondingUpdate = budgetItemUpdatesBeforeCutoff.stream()
                        .filter(x -> !x.isMonthSpecific() || (x.getMonth() == finalMonth && x.getYear() == finalYear))
                        .findFirst();
                if(correspondingUpdate.isPresent()){
                    BudgetItemUpdate budgetItemUpdate = correspondingUpdate.get();
                    budgetItem.setName(budgetItemUpdate.getName());
                    budgetItem.setAmount(budgetItemUpdate.getAmount());
                    displayAmount = budgetItemUpdate.getAmount();
                }

                budgetItem.setTotalSpent(budgetItemService.getAmountSpent(budgetItem.getId(), finalMonth, finalYear));
                budgetItem.setTotalRemaining(budgetItemService.getAmountRemaining(budgetItem.getId(), finalMonth, finalYear, displayAmount));
                List<BudgetEntry> correspondingBudgetEntries;
                if(budgetItem.getBudgetItemType() == BudgetItemType.ANNUAL){
                    correspondingBudgetEntries = budgetItem.getBudgetEntries().stream()
                            .filter(x -> x.getYear() == finalYear && x.getMonth().getMonthNumber() <= finalMonth)
                            .collect(Collectors.toList());
                }
                else{
                    correspondingBudgetEntries = budgetItem.getBudgetEntries().stream()
                            .filter(x -> x.getYear() == finalYear && x.getMonth().getMonthNumber() == finalMonth)
                            .collect(Collectors.toList());
                }
                budgetItem.setBudgetEntries(correspondingBudgetEntries);
            }

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
            if(updatedName.length() < 1){
                throw new RuntimeException();
            }
            existingBudget.setName(updatedName);
        }
        budgetDao.save(existingBudget);
    }

}
