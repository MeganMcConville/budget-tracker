package com.megansportfolio.budgettracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDao.findOneByUsernameIgnoreCase(username);
        if (user != null){
            return new com.megansportfolio.budgettracker.user.UserDetails(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
        throw new UsernameNotFoundException("Username not found.");

    }
}
