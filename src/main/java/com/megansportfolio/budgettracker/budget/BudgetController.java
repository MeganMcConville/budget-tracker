package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

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
        long budgetId = budgetService.createBudget(budget, loggedInUserEmailAddress);
        return "redirect:/budget-items/create?budgetId=" + budgetId;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String viewBudgets(Model model){
        UserDetails loggedInUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        List<Budget> userBudgets = budgetService.findBudgets(loggedInUserEmailAddress);
        if (userBudgets == null || userBudgets.size() == 0){
            return "redirect:/budgets/create";
        }
        model.addAttribute("budgets", userBudgets);
        return "view-budgets";
    }

    @RequestMapping(value = "/{budgetId}", method = RequestMethod.GET)
    public String viewIndividualBudget(@PathVariable long budgetId, Model model){
        UserDetails loggedInUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        Budget individualBudget = budgetService.getBudget(loggedInUserEmailAddress, budgetId);
        model.addAttribute("budget", individualBudget);
        return "individual-budget";
    }
}
