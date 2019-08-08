package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
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

}
