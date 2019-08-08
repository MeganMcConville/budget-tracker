package com.megansportfolio.budgettracker.budgetItemUpdate;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import com.megansportfolio.budgettracker.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class TestBudgetItemUpdateService {

    @InjectMocks
    BudgetItemUpdateService serviceUnderTest;

    @Mock
    BudgetItemDao budgetItemDao;

    @Mock
    BudgetItemUpdateDao budgetItemUpdateDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        serviceUnderTest = new BudgetItemUpdateService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBudgetItemUpdates(){

        long id1 = 1;
        long id2 = 2;
        List<Long> ids = new ArrayList<>();
        ids.add(id1);
        ids.add(id2);

        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        BudgetItemUpdate budgetItemUpdate2 = new BudgetItemUpdate();
        List<BudgetItemUpdate> parameterBudgetItemUpdates = new ArrayList<>();
        parameterBudgetItemUpdates.add(budgetItemUpdate1);
        parameterBudgetItemUpdates.add(budgetItemUpdate2);
        BudgetItem inputItem1 = new BudgetItem();
        BudgetItem inputItem2 = new BudgetItem();
        budgetItemUpdate1.setBudgetItem(inputItem1);
        budgetItemUpdate2.setBudgetItem(inputItem2);
        inputItem1.setId(id1);
        inputItem2.setId(id2);

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        user.setUsername(loggedInUserEmailAddress);
        Budget budget = new Budget();
        budget.setUser(user);

        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setId(id1);
        budgetItem1.setBudget(budget);
        BudgetItem budgetItem2 = new BudgetItem();
        budgetItem2.setId(id2);
        budgetItem2.setBudget(budget);
        List<BudgetItem> originalBudgetItems = new ArrayList<>();
        originalBudgetItems.add(budgetItem1);
        originalBudgetItems.add(budgetItem2);

        Mockito.when(budgetItemDao.findAllById(ids)).thenReturn(originalBudgetItems);

        BudgetItemUpdate existingBudgetItemUpdate = new BudgetItemUpdate();
        int month = 1;
        budgetItemUpdate1.setMonth(month);
        int year = 2018;
        budgetItemUpdate1.setYear(year);
        Mockito.when(budgetItemUpdateDao.findOneByBudgetItemIdAndMonthAndYear(id1, month, year)).thenReturn(existingBudgetItemUpdate);

        serviceUnderTest.createBudgetItemUpdates(parameterBudgetItemUpdates, loggedInUserEmailAddress);

        Mockito.verify(budgetItemUpdateDao).delete(existingBudgetItemUpdate);
        ArgumentCaptor<List<BudgetItemUpdate>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(budgetItemUpdateDao).saveAll(captor.capture());
        List<BudgetItemUpdate> savedBudgetItemUpdates = captor.getValue();
        Assert.assertEquals(2, savedBudgetItemUpdates.size());
        Assert.assertEquals(budgetItemUpdate1, savedBudgetItemUpdates.get(0));
        Assert.assertEquals(budgetItemUpdate2, savedBudgetItemUpdates.get(1));
    }

    @Test
    public void testCreateBudgetItemUpdatesWithNoDeleteExisting(){

        long id1 = 1;
        List<Long> ids = new ArrayList<>();
        ids.add(id1);

        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        List<BudgetItemUpdate> parameterBudgetItemUpdates = new ArrayList<>();
        parameterBudgetItemUpdates.add(budgetItemUpdate1);
        BudgetItem inputItem1 = new BudgetItem();
        budgetItemUpdate1.setBudgetItem(inputItem1);
        inputItem1.setId(id1);

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        user.setUsername(loggedInUserEmailAddress);
        Budget budget = new Budget();
        budget.setUser(user);

        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setId(id1);
        budgetItem1.setBudget(budget);
        List<BudgetItem> originalBudgetItems = new ArrayList<>();
        originalBudgetItems.add(budgetItem1);

        Mockito.when(budgetItemDao.findAllById(ids)).thenReturn(originalBudgetItems);

        int month = 1;
        budgetItemUpdate1.setMonth(month);
        int year = 2018;
        budgetItemUpdate1.setYear(year);
        Mockito.when(budgetItemUpdateDao.findOneByBudgetItemIdAndMonthAndYear(id1, month, year)).thenReturn(null);

        serviceUnderTest.createBudgetItemUpdates(parameterBudgetItemUpdates, loggedInUserEmailAddress);

        Mockito.verify(budgetItemUpdateDao, times(0)).delete(any());
    }

    @Test
    public void testCreateBudgetItemUpdatesWithWrongUser(){

        long id1 = 1;
        List<Long> ids = new ArrayList<>();
        ids.add(id1);

        BudgetItemUpdate budgetItemUpdate1 = new BudgetItemUpdate();
        List<BudgetItemUpdate> parameterBudgetItemUpdates = new ArrayList<>();
        parameterBudgetItemUpdates.add(budgetItemUpdate1);
        BudgetItem inputItem1 = new BudgetItem();
        budgetItemUpdate1.setBudgetItem(inputItem1);
        inputItem1.setId(id1);

        String loggedInUserEmailAddress = "test@test.com";
        String wrongEmailAddress = "wrong@wrong.com";
        User user = new User();
        user.setUsername(wrongEmailAddress);
        Budget budget = new Budget();
        budget.setUser(user);

        BudgetItem budgetItem1 = new BudgetItem();
        budgetItem1.setId(id1);
        budgetItem1.setBudget(budget);
        List<BudgetItem> originalBudgetItems = new ArrayList<>();
        originalBudgetItems.add(budgetItem1);

        Mockito.when(budgetItemDao.findAllById(ids)).thenReturn(originalBudgetItems);

        int month = 1;
        budgetItemUpdate1.setMonth(month);
        int year = 2018;
        budgetItemUpdate1.setYear(year);
        Mockito.when(budgetItemUpdateDao.findOneByBudgetItemIdAndMonthAndYear(id1, month, year)).thenReturn(null);

        expectedException.expect(RuntimeException.class);

        serviceUnderTest.createBudgetItemUpdates(parameterBudgetItemUpdates, loggedInUserEmailAddress);
    }

}
