package com.megansportfolio.budgettracker.sharedUser;

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

}
