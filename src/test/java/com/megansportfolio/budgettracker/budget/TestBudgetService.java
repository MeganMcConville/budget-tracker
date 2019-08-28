package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemService;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestBudgetService {

    @InjectMocks
    BudgetService serviceUnderTest;

    @Mock
    BudgetDao budgetDao;

    @Mock
    UserDao userDao;

    @Mock
    BudgetItemUpdateDao budgetItemUpdateDao;

    @Mock
    BudgetItemService budgetItemService;

    @Mock
    BudgetItemDao budgetItemDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        serviceUnderTest = new BudgetService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBudget(){
        //Arrange
        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        Budget budget = new Budget();
        long budgetId = 1;
        budget.setId(budgetId);
        budget.setUser(user);
        Mockito.when(budgetDao.save(budget)).thenReturn(budget);

        //Act
        long returnedId = serviceUnderTest.createBudget(budget, loggedInUserEmailAddress);

        //Assert
        Assert.assertEquals(budgetId, returnedId);
        Mockito.verify(budgetDao).save(budget);
    }

    @Test
    public void testFindBudgets(){
        //Arrange
        String emailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(emailAddress)).thenReturn(user);

        List<Budget> userBudgets = new ArrayList<>();
        Budget budget1 = new Budget();
        budget1.setUser(user);
        userBudgets.add(budget1);
        Budget budget2 = new Budget();
        budget2.setUser(user);
        userBudgets.add(budget2);
        Mockito.when(budgetDao.findByUser(user)).thenReturn(userBudgets);

        //Act
        List<Budget> resultBudgets = serviceUnderTest.findBudgets(emailAddress);

        //Assert
        Assert.assertEquals(userBudgets, resultBudgets);
    }

    @Test
    public void testGetBudgetWithWrongUser(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long budgetId = 1;
        Budget budget = new Budget();
        Mockito.when(budgetDao.getOne(budgetId)).thenReturn(budget);

        long testUserId = 1;
        user.setId(testUserId);
        User wrongUser = new User();
        long wrongUserId = 2;
        wrongUser.setId(wrongUserId);
        budget.setUser(wrongUser);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.getBudget(loggedInUserEmailAddress, budgetId, null, null);
    }

    @Test
    public void testGetBudgetWithSelectedDateAndBudgetItemUpdates(){
        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        long budgetId = 1;
        Budget budget = new Budget();
        Mockito.when(budgetDao.getOne(budgetId)).thenReturn(budget);

        long testUserId = 1;
        user.setId(testUserId);
        budget.setUser(user);

        List<BudgetItem> budgetItems = new ArrayList<>();
        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setBudgetEntries(new ArrayList<>());
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setBudgetEntries(new ArrayList<>());
        long budgetItem1Id = 1;
        budgetItem1.setId(budgetItem1Id);
        long budgetItem2Id = 2;
        budgetItem2.setId(budgetItem2Id);
        BudgetItem budgetItem3 = new BudgetItem();
        long budgetItem3Id = 3;
        budgetItem3.setId(budgetItem3Id);
        budgetItem3.setBudgetItemType(BudgetItemType.ANNUAL);
        BudgetItem budgetItem4 = new BudgetItem();
        budgetItem4.setBudgetItemType(BudgetItemType.MONTHLY);
        long budgetItem4Id = 4;
        budgetItem4.setId(budgetItem4Id);
        budgetItems.add(budgetItem1);
        budgetItems.add(budgetItem2);
        budgetItems.add(budgetItem3);
        budgetItems.add(budgetItem4);
        budget.setBudgetItems(budgetItems);

        List<BudgetEntry> budgetEntries1 = new ArrayList<>();
        BudgetEntry budgetEntry1 = new BudgetEntry();
        budgetEntry1.setYear(2000);
        budgetEntry1.setMonth(Month.JANUARY);
        budgetEntry1.setAmount(BigDecimal.TEN);
        budgetEntry1.setId(1);
        BudgetEntry budgetEntry2 = new BudgetEntry();
        budgetEntry2.setMonth(Month.JULY);
        budgetEntry2.setYear(2004);
        budgetEntry2.setAmount(BigDecimal.ONE);
        budgetEntry2.setId(2);
        budgetEntries1.add(budgetEntry1);
        budgetEntries1.add(budgetEntry2);
        budgetItem3.setBudgetEntries(budgetEntries1);

        List<BudgetEntry> budgetEntries2 = new ArrayList<>();
        BudgetEntry budgetEntry3 = new BudgetEntry();
        budgetEntry3.setYear(2000);
        budgetEntry3.setMonth(Month.JANUARY);
        budgetEntry3.setAmount(BigDecimal.ONE);
        budgetEntry3.setId(3);
        BudgetEntry budgetEntry4 = new BudgetEntry();
        budgetEntry4.setYear(2000);
        budgetEntry4.setMonth(Month.JUNE);
        budgetEntry4.setAmount(BigDecimal.TEN);
        budgetEntry4.setId(4);
        budgetEntries2.add(budgetEntry3);
        budgetEntries2.add(budgetEntry4);
        budgetItem4.setBudgetEntries(budgetEntries2);

        String testName = "Test";
        BigDecimal testAmount = BigDecimal.TEN;

        //budget item that has no applicable update for selected date
        List<BudgetItemUpdate> budgetItemUpdates1 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        budgetItemUpdate1.setMonth(12);
        budgetItemUpdate1.setYear(2000);
        budgetItemUpdate1.setName(testName);
        budgetItemUpdate1.setMonthSpecific(false);
        budgetItemUpdates1.add(budgetItemUpdate1);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem1Id)).thenReturn(budgetItemUpdates1);

        //budget item with an applicable update for date
        List<BudgetItemUpdate> budgetItemUpdates2 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate2 = new BudgetItemUpdate();
        budgetItemUpdate2.setMonth(12);
        budgetItemUpdate2.setYear(1999);
        budgetItemUpdate2.setMonthSpecific(false);
        budgetItemUpdate2.setName(testName);
        budgetItemUpdate2.setAmount(testAmount);
        budgetItemUpdates2.add(budgetItemUpdate2);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem2Id)).thenReturn(budgetItemUpdates2);

        //budget item with month-specific that isn't applicable
        List<BudgetItemUpdate> budgetItemUpdates3 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate3 = new BudgetItemUpdate();
        budgetItemUpdate3.setMonth(5);
        budgetItemUpdate3.setYear(1999);
        budgetItemUpdate3.setMonthSpecific(true);
        budgetItemUpdate3.setName(testName);
        budgetItemUpdate3.setAmount(testAmount);
        budgetItemUpdates3.add(budgetItemUpdate3);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem3Id)).thenReturn(budgetItemUpdates3);

        //budget item with month-specific that is applicable
        List<BudgetItemUpdate> budgetItemUpdates4 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate4 = new BudgetItemUpdate();
        budgetItemUpdate4.setMonth(1);
        budgetItemUpdate4.setYear(2000);
        budgetItemUpdate4.setName(testName);
        budgetItemUpdate4.setAmount(testAmount);
        budgetItemUpdate4.setMonthSpecific(true);
        budgetItemUpdates4.add(budgetItemUpdate4);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem4Id)).thenReturn(budgetItemUpdates4);

        Mockito.when(budgetItemService.getAmountSpent(budgetItem1Id, 1, 2000)).thenReturn(BigDecimal.TEN);
        Mockito.when(budgetItemService.getAmountRemaining(budgetItem1Id, 1, 2000, null)).thenReturn(BigDecimal.ONE);

        Budget resultBudget = serviceUnderTest.getBudget(loggedInUserEmailAddress, budgetId, 1, 2000);

        Assert.assertEquals(budget, resultBudget);
        Assert.assertNull(budgetItem1.getName());
        Assert.assertNull(budgetItem1.getAmount());
        Assert.assertEquals(BigDecimal.TEN, resultBudget.getBudgetItems().get(0).getTotalSpent());
        Assert.assertEquals(BigDecimal.ONE, resultBudget.getBudgetItems().get(0).getTotalRemaining());
        Assert.assertEquals(testName, budgetItem2.getName());
        Assert.assertEquals(testAmount, budgetItem2.getAmount());
        Assert.assertNull(budgetItem3.getAmount());
        Assert.assertNull(budgetItem3.getName());
        Assert.assertEquals(testName, budgetItem4.getName());
        Assert.assertEquals(testAmount, budgetItem4.getAmount());
        Assert.assertEquals(1, resultBudget.getBudgetItems().get(2).getBudgetEntries().size());
        Assert.assertEquals(1, resultBudget.getBudgetItems().get(2).getBudgetEntries().get(0).getId());
        Assert.assertEquals(1, resultBudget.getBudgetItems().get(3).getBudgetEntries().size());
        Assert.assertEquals(3, resultBudget.getBudgetItems().get(3).getBudgetEntries().get(0).getId());

    }

    @Test
    public void testRenameBudget(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        Budget parameterBudget = new Budget();
        Budget budget = new Budget();
        long budgetId = 1;
        parameterBudget.setId(budgetId);
        budget.setId(budgetId);
        Mockito.when(budgetDao.getOne(parameterBudget.getId())).thenReturn(budget);

        User sameUser = new User();
        long userId = 1;
        user.setId(userId);
        sameUser.setId(userId);
        budget.setUser(sameUser);

        String updatedName = "New Name";
        parameterBudget.setName(updatedName);

        serviceUnderTest.renameBudget(parameterBudget, loggedInUserEmailAddress);

        Mockito.verify(budgetDao).save(budget);
        Assert.assertEquals(updatedName, budget.getName());
    }

    @Test
    public void testRenameBudgetWithWrongUser(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        Budget parameterBudget = new Budget();
        Budget budget = new Budget();
        long budgetId = 1;
        parameterBudget.setId(budgetId);
        budget.setId(budgetId);
        Mockito.when(budgetDao.getOne(parameterBudget.getId())).thenReturn(budget);

        User wrongUser = new User();
        long userId = 1;
        user.setId(userId);
        long wrongUserId = 2;
        wrongUser.setId(wrongUserId);
        budget.setUser(wrongUser);

        expectedException.expect(RuntimeException.class);

        serviceUnderTest.renameBudget(parameterBudget, loggedInUserEmailAddress);
    }

    @Test
    public void testRenamedBudgetWithEmptyName(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        Budget parameterBudget = new Budget();
        Budget budget = new Budget();
        long budgetId = 1;
        parameterBudget.setId(budgetId);
        budget.setId(budgetId);
        Mockito.when(budgetDao.getOne(parameterBudget.getId())).thenReturn(budget);

        User sameUser = new User();
        long userId = 1;
        user.setId(userId);
        sameUser.setId(userId);
        budget.setUser(sameUser);

        String updatedName = "";
        parameterBudget.setName(updatedName);

        expectedException.expect(RuntimeException.class);

        serviceUnderTest.renameBudget(parameterBudget, loggedInUserEmailAddress);

    }

    @Test
    public void testGetDisplayYear(){
        Integer parameterYear = 1800;

        int resultYear = serviceUnderTest.getDisplayYear(parameterYear);

        Assert.assertEquals(parameterYear.intValue(), resultYear);
    }

    @Test
    public void testGetDisplayMonth(){
        Integer parameterMonth = 5;
        Month displayMonth = Month.valueOfMonthNumber(parameterMonth);
        String monthLabel = "May";

        Month resultMonth = serviceUnderTest.getDisplayMonth(parameterMonth);

        Assert.assertEquals(displayMonth, resultMonth);
        Assert.assertEquals(monthLabel, resultMonth.getNameLabel());
    }


}
