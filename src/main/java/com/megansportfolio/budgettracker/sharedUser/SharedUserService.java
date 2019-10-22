package com.megansportfolio.budgettracker.sharedUser;

import com.megansportfolio.budgettracker.user.User;
import com.megansportfolio.budgettracker.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SharedUserService {

    @Autowired
    private SharedUserDao sharedUserDao;

    @Autowired
    private UserDao userDao;

    public boolean hasSharedBudgets(String email){

        List<SharedUser> sharedUsers = sharedUserDao.findAllByEmailIgnoreCase(email);
        return sharedUsers != null && sharedUsers.size() > 0;

    }

    public boolean isSharedUser(String email, long budgetId){
        SharedUser sharedUser = sharedUserDao.findOneByEmailIgnoreCaseAndBudgetId(email, budgetId);
        return sharedUser != null;
    }

    public void deleteSharedUsers(String loggedInUserEmailAddress, List<Long> sharedUserIds){

        User loggedInUser = userDao.findOneByUsernameIgnoreCase(loggedInUserEmailAddress);
        List<SharedUser> sharedUsers = new ArrayList<>();
        for(Long id : sharedUserIds){
            SharedUser sharedUser = sharedUserDao.getOne(id);
            sharedUsers.add(sharedUser);
            if(loggedInUser.getId() != sharedUser.getBudget().getUser().getId()){
                throw new RuntimeException();
            }
        }
        sharedUserDao.deleteAll(sharedUsers);

    }

}
