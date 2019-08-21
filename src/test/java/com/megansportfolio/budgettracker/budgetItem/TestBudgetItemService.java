package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budget.Month;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
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
import java.util.Optional;

import static org.mockito.internal.verification.VerificationModeFactory.times;

public class TestBudgetItemService {

    @InjectMocks
    BudgetItemService serviceUnderTest;

    @Mock
    BudgetItemDao budgetItemDao;

    @Mock
    BudgetDao budgetDao;

    @Mock
    UserDao userDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        serviceUnderTest = new BudgetItemService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBudgetItem(){

        BudgetItem budgetItem = new BudgetItem();
        long budgetId = 1;
        Budget budget = new Budget();
        budget.setId(budgetId);
        budgetItem.setBudget(budget);
        Budget existingBudget = new Budget();
        Mockito.when(budgetDao.findById(budgetItem.getBudget().getId())).thenReturn(Optional.of(existingBudget));

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long userId = 1;
        user.setId(userId);
        existingBudget.setUser(user);

        serviceUnderTest.createBudgetItem(budgetItem, loggedInUserEmailAddress);

        Mockito.verify(budgetItemDao).save(budgetItem);

    }

    @Test
    public void testCreateBudgetItemWithWrongUser(){

        BudgetItem budgetItem = new BudgetItem();
        long budgetId = 1;
        Budget budget = new Budget();
        budget.setId(budgetId);
        budgetItem.setBudget(budget);
        Budget existingBudget = new Budget();
        Mockito.when(budgetDao.findById(budgetItem.getBudget().getId())).thenReturn(Optional.of(existingBudget));

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long userId = 1;
        user.setId(userId);
        User wrongUser = new User();
        long wrongUserId = 2;
        wrongUser.setId(wrongUserId);
        existingBudget.setUser(wrongUser);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.createBudgetItem(budgetItem, loggedInUserEmailAddress);
    }

    @Test
    public void testDeleteBudgetItem(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long budgetItemId = 1;
        BudgetItem budgetItem = new BudgetItem();
        Mockito.when(budgetItemDao.getOne(budgetItemId)).thenReturn(budgetItem);

        long userId = 1;
        Budget budget = new Budget();
        user.setId(userId);
        budget.setUser(user);
        budgetItem.setBudget(budget);

        budgetItem.setId(budgetItemId);

        serviceUnderTest.deleteBudgetItem(budgetItemId, loggedInUserEmailAddress);

        Mockito.verify(budgetItemDao, times(1)).deleteById(budgetItemId);
    }

    @Test
    public void testDeleteBudgetItemWithWrongUser(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long budgetItemId = 1;
        BudgetItem budgetItem = new BudgetItem();
        Mockito.when(budgetItemDao.getOne(budgetItemId)).thenReturn(budgetItem);

        long userId = 1;
        user.setId(userId);
        long wrongUserId = 2;
        User wrongUser = new User();
        Budget budget = new Budget();
        wrongUser.setId(wrongUserId);
        budget.setUser(wrongUser);
        budgetItem.setBudget(budget);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.deleteBudgetItem(budgetItemId, loggedInUserEmailAddress);
    }

    @Test
    public void testGetAmountSpentWhenItemTypeIsAnnual(){

        long budgetItemId = 1;
        BudgetItem budgetItem = new BudgetItem();
        Mockito.when(budgetItemDao.getOne(budgetItemId)).thenReturn(budgetItem);

        BudgetEntry budgetEntry1 = new BudgetEntry();
        budgetEntry1.setAmount(BigDecimal.TEN);
        budgetEntry1.setMonth(Month.MAY);
        budgetEntry1.setYear(2018);
        BudgetEntry budgetEntry2 = new BudgetEntry();
        budgetEntry2.setAmount(BigDecimal.TEN);
        budgetEntry2.setMonth(Month.NOVEMBER);
        budgetEntry2.setYear(2018);
        BudgetEntry budgetEntry3 = new BudgetEntry();
        budgetEntry3.setAmount(BigDecimal.ONE);
        budgetEntry3.setMonth(Month.NOVEMBER);
        budgetEntry3.setYear(2000);
        List<BudgetEntry> budgetEntries = new ArrayList<>();
        budgetEntries.add(budgetEntry1);
        budgetEntries.add(budgetEntry2);
        budgetEntries.add(budgetEntry3);
        budgetItem.setBudgetEntries(budgetEntries);
        budgetItem.setBudgetItemType(BudgetItemType.ANNUAL);

        int parameterMonth = 5;
        int parameterYear = 2018;

        BigDecimal result = serviceUnderTest.getAmountSpent(budgetItemId, parameterMonth, parameterYear);
        BigDecimal expectedResult = new BigDecimal("20");

        Assert.assertEquals(expectedResult, result);
    }

    public void testGetAmountSpentWhenItemTypeIsMontly(){

        long budgetItemId = 1;
        BudgetItem budgetItem = new BudgetItem();
        Mockito.when(budgetItemDao.getOne(budgetItemId)).thenReturn(budgetItem);

        BudgetEntry budgetEntry1 = new BudgetEntry();
        budgetEntry1.setAmount(BigDecimal.TEN);
        budgetEntry1.setMonth(Month.MAY);
        budgetEntry1.setYear(2018);
        BudgetEntry budgetEntry2 = new BudgetEntry();
        budgetEntry2.setAmount(BigDecimal.TEN);
        budgetEntry2.setMonth(Month.NOVEMBER);
        budgetEntry2.setYear(2018);
        BudgetEntry budgetEntry3 = new BudgetEntry();
        budgetEntry3.setAmount(BigDecimal.ONE);
        budgetEntry3.setMonth(Month.NOVEMBER);
        budgetEntry3.setYear(2000);
        List<BudgetEntry> budgetEntries = new ArrayList<>();
        budgetEntries.add(budgetEntry1);
        budgetEntries.add(budgetEntry2);
        budgetEntries.add(budgetEntry3);
        budgetItem.setBudgetEntries(budgetEntries);
        budgetItem.setBudgetItemType(BudgetItemType.MONTHLY);

        int parameterMonth = 5;
        int parameterYear = 2018;

        BigDecimal result = serviceUnderTest.getAmountSpent(budgetItemId, parameterMonth, parameterYear);
        BigDecimal expectedResult = BigDecimal.TEN;

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testGetAmountRemaining(){

        long budgetItemId = 1;
        int month = 5;
        int year = 2018;
        BigDecimal itemAmount = BigDecimal.TEN;

        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setId(budgetItemId);
        BudgetEntry budgetEntry = new BudgetEntry();
        budgetEntry.setYear(2018);
        budgetEntry.setMonth(Month.MAY);
        budgetEntry.setAmount(BigDecimal.TEN);
        List<BudgetEntry> budgetEntries = new ArrayList<>();
        budgetEntries.add(budgetEntry);
        budgetItem.setBudgetEntries(budgetEntries);
        Mockito.when(budgetItemDao.getOne(budgetItemId)).thenReturn(budgetItem);

        BigDecimal result = serviceUnderTest.getAmountRemaining(budgetItemId, month, year, itemAmount);
        BigDecimal expectedResult = BigDecimal.ZERO;

        Assert.assertEquals(expectedResult, result);

    }
}
