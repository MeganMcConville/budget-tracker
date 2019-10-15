package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetService;
import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateService;
import com.megansportfolio.budgettracker.sharedUser.SharedUserService;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetEntryService {

    @Autowired
    private BudgetEntryDao budgetEntryDao;

    @Autowired
    private BudgetItemDao budgetItemDao;

    @Autowired
    private BudgetItemUpdateDao budgetItemUpdateDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SharedUserService sharedUserService;

    public void createBudgetEntry(String loggedInUserEmailAddress, BudgetEntry budgetEntry){
        BudgetItem budgetItem = budgetItemDao.getOne(budgetEntry.getBudgetItem().getId());
        User user = budgetItem.getBudget().getUser();
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        if(user.getId() != loggedInUser.getId() && !sharedUserService.isSharedUser(loggedInUserEmailAddress, budgetItem.getBudget().getId())){
            throw new RuntimeException();
        }
        Month month = Month.valueOfMonthNumber(budgetEntry.getMonthNumber());
        budgetEntry.setMonth(month);
        budgetEntryDao.save(budgetEntry);
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void createRecurringMonthlyEntries(){
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH)) + 1;
        int year = (cal.get(Calendar.YEAR));

        List<BudgetItem> monthlyBudgetItems = budgetItemDao.findAllByBudgetItemType(BudgetItemType.MONTHLY);
        List<BudgetEntry> recurringMonthlyEntries = createRecurringEntries(monthlyBudgetItems, month, year);

        budgetEntryDao.saveAll(recurringMonthlyEntries);
    }

    @Scheduled(cron = "0 0 1 1 1 ?")
    public void createRecurringAnnualEntries(){
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH)) + 1;
        int year = (cal.get(Calendar.YEAR));


        List<BudgetItem> annualBudgetItems = budgetItemDao.findAllByBudgetItemType(BudgetItemType.ANNUAL);
        List<BudgetEntry> recurringAnnualEntries = createRecurringEntries(annualBudgetItems, month, year);

        budgetEntryDao.saveAll(recurringAnnualEntries);
    }

    private List<BudgetEntry> createRecurringEntries(List<BudgetItem> budgetItemsOfType, int month, int year){

        List<BudgetEntry> createdRecurringEntries = new ArrayList<>();

        for(BudgetItem budgetItem : budgetItemsOfType){
            BudgetEntry budgetEntry = new BudgetEntry();
            budgetEntry.setNotes("Recurring charge");
            budgetEntry.setYear(year);
            if(budgetItem.getBudgetItemType() == BudgetItemType.MONTHLY){
                budgetEntry.setMonth(BudgetService.getDisplayMonth(month));
            }
            else{
                budgetEntry.setMonth(Month.JANUARY);
            }

            List<BudgetItemUpdate> budgetItemUpdates = budgetItemUpdateDao.findAllByBudgetItemId(budgetItem.getId());
            Optional<BudgetItemUpdate> correspondingUpdate = BudgetItemUpdateService.findCorrespondingBudgetItemUpdate(year, month, budgetItemUpdates);
            if(correspondingUpdate.isPresent()){
                if(correspondingUpdate.get().isRecurring()) {
                    budgetEntry.setAmount(correspondingUpdate.get().getAmount());
                    budgetEntry.setBudgetItem(budgetItem);
                    createdRecurringEntries.add(budgetEntry);
                }
            }
            else if(budgetItem.isRecurring()){
                budgetEntry.setBudgetItem(budgetItem);
                budgetEntry.setAmount(budgetItem.getAmount());
                createdRecurringEntries.add(budgetEntry);
            }
        }
        return createdRecurringEntries;
    }

}
