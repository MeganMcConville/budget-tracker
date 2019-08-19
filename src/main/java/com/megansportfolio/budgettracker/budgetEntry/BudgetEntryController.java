package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/budget-entries")
public class BudgetEntryController {

    @Autowired
    private BudgetEntryService budgetEntryService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void createBudgetEntry(@RequestBody BudgetEntry budgetEntry){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetEntryService.createBudgetEntry(loggedInUserEmailAddress, budgetEntry);
    }

}
