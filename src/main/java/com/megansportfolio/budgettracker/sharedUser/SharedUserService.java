package com.megansportfolio.budgettracker.sharedUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedUserService {

    @Autowired
    private SharedUserDao sharedUserDao;

    public boolean hasSharedBudgets(String email){

        List<SharedUser> sharedUsers = sharedUserDao.findAllByEmailIgnoreCase(email);
        return sharedUsers != null && sharedUsers.size() > 0;

    }

    public boolean isSharedUser(String email, long budgetId){
        SharedUser sharedUser = sharedUserDao.findOneByEmailIgnoreCaseAndBudgetId(email, budgetId);
        return sharedUser != null;
    }

}
