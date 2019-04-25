package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @RequestMapping(method = RequestMethod.GET, value = "/create")
    public String getCreateBudget(Model model){
        model.addAttribute("budget", new Budget());
        return "create-budget";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createBudget(@ModelAttribute Budget budget){
        UserDetails loggedInUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetService.createBudget(budget, loggedInUserEmailAddress);
        return "create-budget";
    }
}
