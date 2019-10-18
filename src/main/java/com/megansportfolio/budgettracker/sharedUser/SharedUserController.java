package com.megansportfolio.budgettracker.sharedUser;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/shared-users")
public class SharedUserController {

    @Autowired
    private SharedUserService sharedUserService;

    @RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteSharedUsers(@RequestBody List<Long> sharedUserIds){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        sharedUserService.deleteSharedUsers(loggedInUserEmailAddress, sharedUserIds);
    }

}
