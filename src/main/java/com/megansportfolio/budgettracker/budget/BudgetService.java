package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemService;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateService;
import com.megansportfolio.budgettracker.sharedUser.EmailIsCurrentUserException;
import com.megansportfolio.budgettracker.sharedUser.SharedUser;
import com.megansportfolio.budgettracker.sharedUser.SharedUserDao;
import com.megansportfolio.budgettracker.sharedUser.SharedUserService;
import com.megansportfolio.budgettracker.user.InvalidEmailException;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import com.megansportfolio.budgettracker.user.UserService;
import org.apache.commons.validator.routines.EmailValidator;
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

    @Autowired
    private SharedUserDao sharedUserDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SharedUserService sharedUserService;

    public static int getDisplayYear(Integer year){
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

    public static Month getDisplayMonth(Integer month){
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

    public boolean hasBudgets(String emailAddress){
        User user = userDao.findOneByUsernameIgnoreCase(emailAddress);
        List<Budget> budgets = budgetDao.findByUser(user);
        return budgets != null && budgets.size() > 0;
    }

    public List<Integer> getActiveBudgetYears(long budgetId){
        Budget budget = budgetDao.getOne(budgetId);
        List<Long> budgetItemIds = budget.getBudgetItems().stream()
                .map(BudgetItem::getId)
                .collect(Collectors.toList());
        if(!budgetItemIds.isEmpty()){
            List<BigDecimal> years = budgetDao.findActiveYearsByBudgetItemId(budgetItemIds);
            List<Integer> activeYears = years.stream()
                    .map(BigDecimal::intValue)
                    .collect(Collectors.toList());
            return activeYears;
        }
        else{
            List<Integer> emptyList = new ArrayList<>();
            return emptyList;
        }
    }

    public List<Budget> findSharedBudgets(String emailAddress){
        List<SharedUser> sharedUsers = sharedUserDao.findAllByEmailIgnoreCase(emailAddress);
        List<Budget> sharedBudgets = sharedUsers.stream()
                .map(x -> x.getBudget())
                .collect(Collectors.toList());
        return sharedBudgets;
    }

    @Transactional(readOnly = true)
    public Budget getBudget(String loggedInUserEmailAddress, long budgetId, Integer month, Integer year){
        User currentUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        Budget budget = budgetDao.getOne(budgetId);
        if(currentUser.getId() == budget.getUser().getId() || sharedUserService.isSharedUser(loggedInUserEmailAddress, budgetId)){
            if(month == null || year == null){
                Calendar cal = Calendar.getInstance();
                month = (cal.get(Calendar.MONTH)) + 1;
                year = (cal.get(Calendar.YEAR));
            }

            List<BudgetItem> budgetItems = budget.getBudgetItems();
            final int finalMonth = month;
            final int finalYear = year;

            for(BudgetItem budgetItem : budgetItems){
                BigDecimal displayAmount = budgetItem.getAmount();
                List<BudgetItemUpdate> budgetItemUpdates = budgetItemUpdateDao.findAllByBudgetItemId(budgetItem.getId());

                Optional<BudgetItemUpdate> correspondingUpdate = BudgetItemUpdateService.findCorrespondingBudgetItemUpdate(finalYear, finalMonth, budgetItemUpdates);
                if(correspondingUpdate.isPresent()){
                    BudgetItemUpdate budgetItemUpdate = correspondingUpdate.get();
                    budgetItem.setName(budgetItemUpdate.getName());
                    budgetItem.setAmount(budgetItemUpdate.getAmount());
                    displayAmount = budgetItemUpdate.getAmount();
                    budgetItem.setRecurring(budgetItemUpdate.isRecurring());
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

    public void renameBudget(Budget budget, String loggedInUserEmailAddress){
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        Budget existingBudget = budgetDao.getOne(budget.getId());
        if(loggedInUser.getId() != existingBudget.getUser().getId() && !sharedUserService.isSharedUser(loggedInUserEmailAddress, existingBudget.getId())){
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

    public Long addSharedUser(String loggedInUserEmailAddress, String searchedEmailAddress, long budgetId) throws InvalidEmailException, EmailIsCurrentUserException {
        Budget budget = budgetDao.getOne(budgetId);
        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        if(loggedInUser.getId() != budget.getUser().getId()){
            throw new RuntimeException();
        }
        if(loggedInUserEmailAddress.equals(searchedEmailAddress)){
            throw new EmailIsCurrentUserException();
        }
        if(!userService.isEmailValid(searchedEmailAddress)){
            throw new InvalidEmailException();
        }
        SharedUser searchedUser = sharedUserDao.findOneByEmailIgnoreCaseAndBudgetId(searchedEmailAddress, budgetId);

        if(searchedUser != null){
            return null;
        }

        else{
            SharedUser sharedUser = new SharedUser();
            sharedUser.setBudget(budget);
            sharedUser.setEmail(searchedEmailAddress);
            return sharedUserDao.save(sharedUser).getId();
        }
    }

}
