package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budgetItemHistory.BudgetItemHistory;
import com.megansportfolio.budgettracker.budgetItemHistory.BudgetItemHistoryDao;
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

    @Mock
    BudgetItemHistoryDao budgetItemHistoryDao;

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
    public void testUpdateBudgetItems(){

        long id1 = 1;
        long id2 = 2;
        long id3 = 3;
        long id4 = 4;
        List<Long> ids = new ArrayList<>();
        ids.add(id1);
        ids.add(id2);
        ids.add(id3);
        ids.add(id4);

        BudgetItem budgetItem1 = new BudgetItem();
        BudgetItem budgetItem2 = new BudgetItem();
        BudgetItem budgetItem3 = new BudgetItem();
        BudgetItem budgetItem4 = new BudgetItem();
        List<BudgetItem> originalBudgetItems = new ArrayList<>();
        originalBudgetItems.add(budgetItem1);
        originalBudgetItems.add(budgetItem2);
        originalBudgetItems.add(budgetItem3);
        originalBudgetItems.add(budgetItem4);
        Mockito.when(budgetItemDao.findAllById(ids)).thenReturn(originalBudgetItems);

        BudgetItem newBudgetItem1 = new BudgetItem();
        BudgetItem newBudgetItem2 = new BudgetItem();
        BudgetItem newBudgetItem3 = new BudgetItem();
        BudgetItem newBudgetItem4 = new BudgetItem();
        List<BudgetItem> parameterBudgetItems = new ArrayList<>();
        parameterBudgetItems.add(newBudgetItem1);
        parameterBudgetItems.add(newBudgetItem2);
        parameterBudgetItems.add(newBudgetItem3);
        parameterBudgetItems.add(newBudgetItem4);

        budgetItem1.setId(id1);
        newBudgetItem1.setId(id1);
        budgetItem2.setId(id2);
        newBudgetItem2.setId(id2);
        budgetItem3.setId(id3);
        newBudgetItem3.setId(id3);
        budgetItem4.setId(id4);
        newBudgetItem4.setId(id4);

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        user.setUsername(loggedInUserEmailAddress);
        Budget budget = new Budget();
        budget.setUser(user);

        originalBudgetItems.forEach(x -> x.setBudget(budget));

        // first: nothing overridden
        budgetItem1.setName("Name");
        budgetItem1.setAmount(BigDecimal.ONE);

        // second: name overridden
        budgetItem2.setName("Name");
        budgetItem2.setAmount(BigDecimal.ONE);
        newBudgetItem2.setName("New Name");

        // third: amount overridden
        budgetItem3.setName("Name");
        budgetItem3.setAmount(BigDecimal.ONE);
        newBudgetItem3.setAmount(BigDecimal.TEN);

        // fourth: both overridden
        budgetItem4.setName("Name");
        budgetItem4.setAmount(BigDecimal.ONE);
        newBudgetItem4.setName("New Name");
        newBudgetItem4.setAmount(BigDecimal.TEN);

        serviceUnderTest.updateBudgetItems(parameterBudgetItems, loggedInUserEmailAddress);

        ArgumentCaptor<List<BudgetItemHistory>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(budgetItemHistoryDao).saveAll(captor.capture());
        List<BudgetItemHistory> savedBudgetItemHistories = captor.getValue();
        Assert.assertEquals(3, savedBudgetItemHistories.size());
        Assert.assertEquals("Name", savedBudgetItemHistories.get(0).getName());
        Assert.assertEquals(BigDecimal.ONE, savedBudgetItemHistories.get(1).getAmount());
        Assert.assertEquals("Name", savedBudgetItemHistories.get(2).getName());
        Assert.assertEquals(BigDecimal.ONE, savedBudgetItemHistories.get(2).getAmount());

        Assert.assertEquals("Name", budgetItem1.getName());
        Assert.assertEquals(BigDecimal.ONE, budgetItem1.getAmount());
        Assert.assertEquals(newBudgetItem2.getName(), budgetItem2.getName());
        Assert.assertEquals(newBudgetItem3.getAmount(), budgetItem3.getAmount());
        Assert.assertEquals(newBudgetItem4.getName(), budgetItem4.getName());
        Assert.assertEquals(newBudgetItem4.getAmount(), budgetItem4.getAmount());

    }

    @Test
    public void testUpdateBudgetItemsWithWrongUser(){

        long id1 = 1;
        long id2 = 2;
        long id3 = 3;
        long id4 = 4;
        List<Long> ids = new ArrayList<>();
        ids.add(id1);
        ids.add(id2);
        ids.add(id3);
        ids.add(id4);

        BudgetItem budgetItem1 = new BudgetItem();
        BudgetItem budgetItem2 = new BudgetItem();
        BudgetItem budgetItem3 = new BudgetItem();
        BudgetItem budgetItem4 = new BudgetItem();
        List<BudgetItem> originalBudgetItems = new ArrayList<>();
        originalBudgetItems.add(budgetItem1);
        originalBudgetItems.add(budgetItem2);
        originalBudgetItems.add(budgetItem3);
        originalBudgetItems.add(budgetItem4);
        Mockito.when(budgetItemDao.findAllById(ids)).thenReturn(originalBudgetItems);

        BudgetItem newBudgetItem1 = new BudgetItem();
        BudgetItem newBudgetItem2 = new BudgetItem();
        BudgetItem newBudgetItem3 = new BudgetItem();
        BudgetItem newBudgetItem4 = new BudgetItem();
        List<BudgetItem> parameterBudgetItems = new ArrayList<>();
        parameterBudgetItems.add(newBudgetItem1);
        parameterBudgetItems.add(newBudgetItem2);
        parameterBudgetItems.add(newBudgetItem3);
        parameterBudgetItems.add(newBudgetItem4);

        budgetItem1.setId(id1);
        newBudgetItem1.setId(id1);
        budgetItem2.setId(id2);
        newBudgetItem2.setId(id2);
        budgetItem3.setId(id3);
        newBudgetItem3.setId(id3);
        budgetItem4.setId(id4);
        newBudgetItem4.setId(id4);

        String loggedInUserEmailAddress = "test@test.com";
        User wrongUser = new User();
        wrongUser.setUsername("wrong@wrong.com");
        Budget budget = new Budget();
        budget.setUser(wrongUser);

        originalBudgetItems.forEach(x -> x.setBudget(budget));

        expectedException.expect(RuntimeException.class);

        serviceUnderTest.updateBudgetItems(parameterBudgetItems, loggedInUserEmailAddress);

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
