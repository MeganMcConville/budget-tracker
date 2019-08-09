package com.megansportfolio.budgettracker.budgetItemUpdate;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/budget-item-updates")
public class BudgetItemUpdateController {

    @Autowired
    private BudgetItemUpdateService budgetItemUpdateService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createBudgetItemUpdates(@RequestBody List<BudgetItemUpdate> budgetItemUpdates){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetItemUpdateService.createBudgetItemUpdates(budgetItemUpdates, loggedInUserEmailAddress);
    }

}
