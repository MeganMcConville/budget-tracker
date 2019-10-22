package com.megansportfolio.budgettracker.sharedUser;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class TestSharedUserService {

    @InjectMocks
    SharedUserService serviceUnderTest;

    @Mock
    SharedUserDao sharedUserDao;

    @Mock
    UserDao userDao;

    @Before
    public void setup(){
        serviceUnderTest = new SharedUserService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHasSharedBudgets(){
        String emailAddress = "email@email.com";
        List<SharedUser> sharedUsers = new ArrayList<>();
        SharedUser sharedUser = new SharedUser();
        sharedUser.setEmail(emailAddress);
        sharedUsers.add(sharedUser);
        Mockito.when(sharedUserDao.findAllByEmailIgnoreCase(emailAddress)).thenReturn(sharedUsers);

        boolean result = serviceUnderTest.hasSharedBudgets(emailAddress);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsSharedUser(){
        String emailAddress = "email@email.com";
        long budgetId = 1;
        SharedUser sharedUser = new SharedUser();
        Mockito.when(sharedUserDao.findOneByEmailIgnoreCaseAndBudgetId(emailAddress, budgetId)).thenReturn(sharedUser);

        boolean result = serviceUnderTest.isSharedUser(emailAddress, budgetId);

        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteSharedUsers(){

        String emailAddress = "email@email.com";
        List<Long> sharedUserIds = new ArrayList<>();
        long id1 = 4;
        long id2 = 2;
        sharedUserIds.add(id1);
        sharedUserIds.add(id2);

        User loggedInUser = new User();
        long loggedInId = 5;
        loggedInUser.setId(loggedInId);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(emailAddress)).thenReturn(loggedInUser);

        Budget budget = new Budget();
        long budgetId = 9;
        budget.setId(budgetId);
        budget.setUser(loggedInUser);


        SharedUser sharedUser1 = new SharedUser();
        SharedUser sharedUser2 = new SharedUser();
        sharedUser1.setBudget(budget);
        sharedUser2.setBudget(budget);
        Mockito.when(sharedUserDao.getOne(id1)).thenReturn(sharedUser1);
        Mockito.when(sharedUserDao.getOne(id2)).thenReturn(sharedUser2);

        List<SharedUser> usersToDelete = new ArrayList<>();
        usersToDelete.add(sharedUser1);
        usersToDelete.add(sharedUser2);

        serviceUnderTest.deleteSharedUsers(emailAddress, sharedUserIds);

        Mockito.verify(sharedUserDao, Mockito.times(1)).deleteAll(usersToDelete);

    }

}
