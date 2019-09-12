package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budget.BudgetService;
import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateService;
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
    BudgetEntryDao budgetEntryDao;

    @Autowired
    BudgetItemDao budgetItemDao;

    @Autowired
    BudgetItemUpdateDao budgetItemUpdateDao;

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
        budgetEntry.setMonth(month);
        budgetEntryDao.save(budgetEntry);
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void createRecurringMonthlyEntry(){
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH)) + 1;
        int year = (cal.get(Calendar.YEAR));

        List<BudgetEntry> createdRecurringEntries = new ArrayList<>();

        List<BudgetItem> monthlyBudgetItems = budgetItemDao.findAllByBudgetItemType(BudgetItemType.MONTHLY);
        for(BudgetItem budgetItem : monthlyBudgetItems){
            BudgetEntry budgetEntry = new BudgetEntry();
            budgetEntry.setNotes("Recurring charge");
            budgetEntry.setMonth(BudgetService.getDisplayMonth(month));
            budgetEntry.setYear(year);

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
        budgetEntryDao.saveAll(createdRecurringEntries);
    }

    @Scheduled(cron = "0 0 1 1 1 ?")
    public void createRecurringAnnualEntry(){
        Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH)) + 1;
        int year = (cal.get(Calendar.YEAR));

        List<BudgetEntry> createdRecurringEntries = new ArrayList<>();

        List<BudgetItem> annualBudgetItems = budgetItemDao.findAllByBudgetItemType(BudgetItemType.ANNUAL);
        for(BudgetItem budgetItem : annualBudgetItems){
            BudgetEntry budgetEntry = new BudgetEntry();
            budgetEntry.setNotes("Recurring charge");
            budgetEntry.setMonth(Month.JANUARY);
            budgetEntry.setYear(year);

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

        budgetEntryDao.saveAll(createdRecurringEntries);
    }

}
