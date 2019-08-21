package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.math.BigDecimal;

public class TestBudgetEntryService {

    @InjectMocks
    BudgetEntryService serviceUnderTest;

    @Mock
    UserDao userDao;

    @Mock
    BudgetItemDao budgetItemDao;

    @Mock
    BudgetEntryDao budgetEntryDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        serviceUnderTest = new BudgetEntryService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBudgetEntry(){

        BudgetItem budgetItem = new BudgetItem();
        BudgetEntry budgetEntry = new BudgetEntry();
        budgetEntry.setBudgetItem(budgetItem);
        long budgetItemId = 1;
        budgetItem.setId(budgetItemId);
        Mockito.when(budgetItemDao.getOne(budgetEntry.getBudgetItem().getId())).thenReturn(budgetItem);

        User user = new User();
        Budget budget = new Budget();
        budgetItem.setBudget(budget);
        budget.setUser(user);

        User loggedInUser = new User();
        String loggedInUserEmailAddress = "test@test.com";
        loggedInUser.setUsername(loggedInUserEmailAddress);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(loggedInUser);

        long userId = 1;
        loggedInUser.setId(userId);
        user.setId(userId);

        BigDecimal amount = BigDecimal.TEN;
        int year = 2019;
        String notes = "These are notes.";
        int monthNumber = 1;
        budgetEntry.setAmount(amount);
        budgetEntry.setMonthNumber(monthNumber);
        budgetEntry.setYear(year);
        budgetEntry.setNotes(notes);

        serviceUnderTest.createBudgetEntry(loggedInUserEmailAddress, budgetEntry);

        ArgumentCaptor<BudgetEntry> captor = ArgumentCaptor.forClass(BudgetEntry.class);
        Mockito.verify(budgetEntryDao).save(captor.capture());
        BudgetEntry savedBudgetEntry = captor.getValue();
        Assert.assertEquals(Month.JANUARY, savedBudgetEntry.getMonth());
        Assert.assertEquals(notes, savedBudgetEntry.getNotes());
        Assert.assertEquals(year, savedBudgetEntry.getYear());
        Assert.assertEquals(amount, savedBudgetEntry.getAmount());
    }

    @Test
    public void testCreateBudgetEntryWithWrongUser(){

        BudgetItem budgetItem = new BudgetItem();
        BudgetEntry budgetEntry = new BudgetEntry();
        budgetEntry.setBudgetItem(budgetItem);
        long budgetItemId = 1;
        budgetItem.setId(budgetItemId);
        Mockito.when(budgetItemDao.getOne(budgetEntry.getBudgetItem().getId())).thenReturn(budgetItem);

        User user = new User();
        Budget budget = new Budget();
        budgetItem.setBudget(budget);
        budget.setUser(user);

        User loggedInUser = new User();
        String loggedInUserEmailAddress = "test@test.com";
        loggedInUser.setUsername(loggedInUserEmailAddress);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(loggedInUser);

        long userId = 1;
        long wrongUserId = 2;
        loggedInUser.setId(wrongUserId);
        user.setId(userId);

        expectedException.expect(RuntimeException.class);

        serviceUnderTest.createBudgetEntry(loggedInUserEmailAddress, budgetEntry);
    }

}
