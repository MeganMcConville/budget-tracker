package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestBudgetEntryService {

    @InjectMocks
    BudgetEntryService serviceUnderTest;

    @Mock
    UserDao userDao;

    @Mock
    BudgetItemDao budgetItemDao;

    @Mock
    BudgetEntryDao budgetEntryDao;

    @Mock
    BudgetItemUpdateDao budgetItemUpdateDao;

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

    @Test
    public void testCreateRecurringMonthlyEntry(){

        int month = 5;
        int year = 2018;

        List<BudgetEntry> createdRecurringEntries = new ArrayList<>();

        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setBudgetItemType(BudgetItemType.MONTHLY);
        budgetItem1.setRecurring(true);
        budgetItem1.setAmount(BigDecimal.TEN);
        budgetItem1.setId(1);
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setBudgetItemType(BudgetItemType.MONTHLY);
        budgetItem2.setAmount(BigDecimal.ONE);
        budgetItem2.setRecurring(true);
        budgetItem2.setId(2);
        List<BudgetItem> monthlyBudgetItems = new ArrayList<>();
        monthlyBudgetItems.add(budgetItem1);
        monthlyBudgetItems.add(budgetItem2);
        Mockito.when(budgetItemDao.findAllByBudgetItemType(BudgetItemType.MONTHLY)).thenReturn(monthlyBudgetItems);

        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        budgetItemUpdate1.setRecurring(true);
        budgetItemUpdate1.setMonthSpecific(false);
        budgetItemUpdate1.setMonth(4);
        budgetItemUpdate1.setYear(2018);
        budgetItemUpdate1.setBudgetItem(budgetItem1);
        budgetItemUpdate1.setAmount(BigDecimal.valueOf(5));
        BudgetItemUpdate budgetItemUpdate2 = new BudgetItemUpdate();
        budgetItemUpdate2.setRecurring(false);
        budgetItemUpdate2.setMonthSpecific(true);
        budgetItemUpdate2.setMonth(12);
        budgetItemUpdate2.setYear(2015);
        budgetItemUpdate2.setBudgetItem(budgetItem1);
        budgetItemUpdate2.setAmount(BigDecimal.ONE);
        List<BudgetItemUpdate> budgetItemUpdates1 = new ArrayList<>();
        budgetItemUpdates1.add(budgetItemUpdate1);
        budgetItemUpdates1.add(budgetItemUpdate2);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem1.getId())).thenReturn(budgetItemUpdates1);

        List<BudgetItemUpdate> budgetItemUpdates2 = new ArrayList<>();
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem2.getId())).thenReturn(budgetItemUpdates2);

        serviceUnderTest.createRecurringMonthlyEntries();

        ArgumentCaptor<List<BudgetEntry>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(budgetEntryDao).saveAll(captor.capture());
        List<BudgetEntry> createdEntries = captor.getValue();

        Assert.assertEquals(2, createdEntries.size());
        Assert.assertEquals(BigDecimal.valueOf(5), createdEntries.get(0).getAmount());
        Assert.assertEquals(BigDecimal.ONE, createdEntries.get(1).getAmount());
    }

    @Test
    public void testCreateRecurringAnnualEntry(){
        int month = 1;
        int year = 2018;

        List<BudgetEntry> createdRecurringEntries = new ArrayList<>();

        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setBudgetItemType(BudgetItemType.ANNUAL);
        budgetItem1.setRecurring(true);
        budgetItem1.setAmount(BigDecimal.TEN);
        budgetItem1.setId(1);
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setBudgetItemType(BudgetItemType.ANNUAL);
        budgetItem2.setAmount(BigDecimal.ONE);
        budgetItem2.setRecurring(true);
        budgetItem2.setId(2);
        List<BudgetItem> annualBudgetItems = new ArrayList<>();
        annualBudgetItems.add(budgetItem1);
        annualBudgetItems.add(budgetItem2);
        Mockito.when(budgetItemDao.findAllByBudgetItemType(BudgetItemType.ANNUAL)).thenReturn(annualBudgetItems);

        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        budgetItemUpdate1.setRecurring(true);
        budgetItemUpdate1.setMonthSpecific(false);
        budgetItemUpdate1.setMonth(12);
        budgetItemUpdate1.setYear(2017);
        budgetItemUpdate1.setBudgetItem(budgetItem1);
        budgetItemUpdate1.setAmount(BigDecimal.valueOf(5));
        BudgetItemUpdate budgetItemUpdate2 = new BudgetItemUpdate();
        budgetItemUpdate2.setRecurring(false);
        budgetItemUpdate2.setMonthSpecific(true);
        budgetItemUpdate2.setMonth(12);
        budgetItemUpdate2.setYear(2015);
        budgetItemUpdate2.setBudgetItem(budgetItem1);
        budgetItemUpdate2.setAmount(BigDecimal.ONE);
        List<BudgetItemUpdate> budgetItemUpdates1 = new ArrayList<>();
        budgetItemUpdates1.add(budgetItemUpdate1);
        budgetItemUpdates1.add(budgetItemUpdate2);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem1.getId())).thenReturn(budgetItemUpdates1);

        List<BudgetItemUpdate> budgetItemUpdates2 = new ArrayList<>();
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem2.getId())).thenReturn(budgetItemUpdates2);

        serviceUnderTest.createRecurringAnnualEntries();

        ArgumentCaptor<List<BudgetEntry>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(budgetEntryDao).saveAll(captor.capture());
        List<BudgetEntry> createdEntries = captor.getValue();

        Assert.assertEquals(2, createdEntries.size());
        Assert.assertEquals(BigDecimal.valueOf(5), createdEntries.get(0).getAmount());
        Assert.assertEquals(BigDecimal.ONE, createdEntries.get(1).getAmount());
    }

}
