package com.megansportfolio.budgettracker.user;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUserService {

    @InjectMocks
    UserService serviceUnderTest;

    @Mock
    UserDao userDao;

    @Mock
    PasswordEncoder passwordEncoder;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup(){
        serviceUnderTest = new UserService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser() throws EmailExistsException, InvalidEmailException {

        User user = new User();
        String userEmail = "test@test.com";
        user.setUsername(userEmail);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(user.getUsername())).thenReturn(null);

        String userPassword = "password";
        user.setPassword(userPassword);
        String firstName = "Test";
        user.setFirstName(firstName);
        String lastName = "Tester";
        user.setLastName(lastName);

        serviceUnderTest.createUser(user);

        Mockito.verify(userDao).save(user);
    }

    @Test
    public void testCreateUserWithExistingEmail() throws EmailExistsException, InvalidEmailException {

        User user = new User();
        String userEmail = "test@test.com";
        user.setUsername(userEmail);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(user.getUsername())).thenReturn(user);
        expectedException.expect(EmailExistsException.class);

        serviceUnderTest.createUser(user);
    }

    @Test
    public void testCreateUserWithEmptyFirstName() throws EmailExistsException, InvalidEmailException {

        User user = new User();
        String userEmail = "test@test.com";
        user.setUsername(userEmail);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(user.getUsername())).thenReturn(null);

        String userPassword = "password";
        user.setPassword(userPassword);
        String firstName = "  ";
        user.setFirstName(firstName);
        String lastName = "Tester";
        user.setLastName(lastName);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.createUser(user);
    }

    @Test
    public void testCreateUserWithEmptyLastName() throws EmailExistsException, InvalidEmailException {
        User user = new User();
        String userEmail = "test@test.com";
        user.setUsername(userEmail);
        Mockito.when(userDao.findOneByUsernameIgnoreCase(user.getUsername())).thenReturn(null);

        String userPassword = "password";
        user.setPassword(userPassword);
        String firstName = "Test";
        user.setFirstName(firstName);
        String lastName = "   ";
        user.setLastName(lastName);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.createUser(user);
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws EmailExistsException, InvalidEmailException {

        User user = new User();
        String invalidEmail = "testtest";
        user.setUsername(invalidEmail);
        expectedException.expect(InvalidEmailException.class);

        serviceUnderTest.createUser(user);
    }

    @Test
    public void testGetUser(){

        String loggedInUserEmailAddress = "test@test.com";
        User user = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(user);

        User resultUser = serviceUnderTest.getUser(loggedInUserEmailAddress);

        Assert.assertEquals(user, resultUser);
    }

    @Test
    public void testUpdateUser(){

        String loggedInUserEmailAddress = "test@test.com";
        User parameterUser = new User();
        User loggedInUser = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(loggedInUser);

        String firstName = "Test";
        String lastName = "Tester";
        parameterUser.setFirstName(firstName);
        parameterUser.setLastName(lastName);

        serviceUnderTest.updateUser(parameterUser, loggedInUserEmailAddress);

        Mockito.verify(userDao).save(loggedInUser);
        Assert.assertEquals(firstName, loggedInUser.getFirstName());
        Assert.assertEquals(lastName, loggedInUser.getLastName());
    }

    @Test
    public void testUpdateUserWithEmptyFirstName(){

        String loggedInUserEmailAddress = "test@test.com";
        User parameterUser = new User();
        User loggedInUser = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(loggedInUser);

        String firstName = "   ";
        String lastName = "Tester";
        parameterUser.setFirstName(firstName);
        parameterUser.setLastName(lastName);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.updateUser(parameterUser, loggedInUserEmailAddress);
    }

    @Test
    public void testUpdateUserWithEmptyLastName(){

        String loggedInUserEmailAddress = "test@test.com";
        User parameterUser = new User();
        User loggedInUser = new User();
        Mockito.when(userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress)).thenReturn(loggedInUser);

        String firstName = "Test";
        String lastName = "    ";
        parameterUser.setFirstName(firstName);
        parameterUser.setLastName(lastName);
        expectedException.expect(RuntimeException.class);

        serviceUnderTest.updateUser(parameterUser, loggedInUserEmailAddress);
    }

}
