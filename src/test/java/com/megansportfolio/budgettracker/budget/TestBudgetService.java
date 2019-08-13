package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
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
    public void testGetBudgetWithDefaultDate(){
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
        BudgetItem budgetItem2 = new BudgetItem();
        long budgetItem1Id = 1;
        budgetItem1.setId(budgetItem1Id);
        long budgetItem2Id = 2;
        budgetItem2.setId(budgetItem2Id);
        budgetItems.add(budgetItem1);
        budgetItems.add(budgetItem2);
        budget.setBudgetItems(budgetItems);

        List<BudgetItemUpdate> budgetItemUpdates = new ArrayList<>();
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem1Id)).thenReturn(budgetItemUpdates);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem2Id)).thenReturn(budgetItemUpdates);

        Budget resultBudget = serviceUnderTest.getBudget(loggedInUserEmailAddress, budgetId, null, null);

        Assert.assertEquals(budget, resultBudget);
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
        BudgetItem budgetItem2 = new BudgetItem();
        long budgetItem1Id = 1;
        budgetItem1.setId(budgetItem1Id);
        long budgetItem2Id = 2;
        budgetItem2.setId(budgetItem2Id);
        budgetItems.add(budgetItem1);
        budgetItems.add(budgetItem2);
        budget.setBudgetItems(budgetItems);

        String testName = "Test";
        List<BudgetItemUpdate> budgetItemUpdates1 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        budgetItemUpdate1.setMonth(12);
        budgetItemUpdate1.setYear(2000);
        budgetItemUpdate1.setName(testName);
        budgetItemUpdates1.add(budgetItemUpdate1);
        List<BudgetItemUpdate> budgetItemUpdates2 = new ArrayList<>();
        BudgetItemUpdate budgetItemUpdate2 = new BudgetItemUpdate();
        budgetItemUpdate2.setMonth(12);
        budgetItemUpdate2.setYear(1999);
        budgetItemUpdate2.setMonthSpecific(false);
        budgetItemUpdate2.setName(testName);
        budgetItemUpdates2.add(budgetItemUpdate2);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem1Id)).thenReturn(budgetItemUpdates1);
        Mockito.when(budgetItemUpdateDao.findAllByBudgetItemId(budgetItem2Id)).thenReturn(budgetItemUpdates2);

        Budget resultBudget = serviceUnderTest.getBudget(loggedInUserEmailAddress, budgetId, 1, 2000);

        Assert.assertEquals(budget, resultBudget);
        Assert.assertEquals(testName, budgetItem2.getName());
        Assert.assertEquals(null, budgetItem1.getName());

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
