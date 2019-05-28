package com.megansportfolio.budgettracker.user;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(User user) throws EmailExistsException, InvalidEmailException{

        User existingUser = userDao.findOneByUsernameIgnoreCase(user.getUsername());
        if(existingUser != null){
            throw new EmailExistsException();
        }

        EmailValidator validator = EmailValidator.getInstance();
        if (validator.isValid(user.getUsername())){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setFirstName(user.getFirstName().trim());
            user.setLastName(user.getLastName().trim());
            if(user.getFirstName().length() < 1 || user.getLastName().length() < 1){
                throw new RuntimeException();
            }
            userDao.save(user);
        }
        else{
            throw new InvalidEmailException();
        }


    }

    public User getUser(String loggedInUserUsername){
        User currentUser = userDao.findOneByUsernameIgnoreCase(loggedInUserUsername);
        return currentUser;
    }
}
