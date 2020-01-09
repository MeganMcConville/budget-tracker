package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.sharedUser.EmailIsCurrentUserException;
import com.megansportfolio.budgettracker.sharedUser.SharedUserService;
import com.megansportfolio.budgettracker.surroundingDates.SurroundingDates;
import com.megansportfolio.budgettracker.surroundingDates.SurroundingDatesService;
import com.megansportfolio.budgettracker.user.InvalidEmailException;
import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private SharedUserService sharedUserService;

    @Autowired
    private SurroundingDatesService surroundingDatesService;

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

    @RequestMapping(value = "/shared", method = RequestMethod.GET)
    public String viewSharedBudgets(Model model){
        UserDetails loggedInUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        List<Budget> sharedBudgets = budgetService.findSharedBudgets(loggedInUserEmailAddress);
        model.addAttribute("sharedBudgets", sharedBudgets);
        return "view-shared-budgets";
    }


    @RequestMapping(value = "/{budgetId}", method = RequestMethod.GET)
    public String viewIndividualBudget(@PathVariable long budgetId, @RequestParam(required = false) Integer month,
                                       @RequestParam(required = false) Integer year, Model model,
                                       @RequestParam(required = false, defaultValue = "false") Boolean tableOnly){
        UserDetails loggedInUser = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        model.addAttribute("isSharedUser", sharedUserService.isSharedUser(loggedInUserEmailAddress, budgetId));
        Budget individualBudget = budgetService.getBudget(loggedInUserEmailAddress, budgetId, month, year);
        Month displayMonth = budgetService.getDisplayMonth(month);
        int displayYear = budgetService.getDisplayYear(year);
        SurroundingDates surroundingDates = surroundingDatesService.getSurroundingDates(month, year);
        model.addAttribute("budget", individualBudget);
        model.addAttribute("displayYear", displayYear);
        model.addAttribute("displayMonth", displayMonth);
        model.addAttribute("surroundingDates", surroundingDates);
        if(tableOnly){
            return "fragments/budget-items-table";
        }
        return "budget";
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void renameBudget(@RequestBody Budget budget){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetService.renameBudget(budget, loggedInUserEmailAddress);
    }

    @RequestMapping(value = "/{budgetId}/share", method = RequestMethod.POST)
    @ResponseBody
    public Long addSharedUser(@RequestParam("searchedEmailAddress") String searchedEmailAddress, @PathVariable long budgetId) throws InvalidEmailException, EmailIsCurrentUserException {
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        return budgetService.addSharedUser(loggedInUserEmailAddress, searchedEmailAddress, budgetId);
    }

}
