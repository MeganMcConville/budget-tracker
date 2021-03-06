package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/budget-items")
public class BudgetItemController {

    @Autowired
    private BudgetItemService budgetItemService;

    @RequestMapping(method = RequestMethod.GET, value = "/create")
    public String getCreateBudgetItem(@RequestParam int budgetId, Model model){
        model.addAttribute("budgetId", budgetId);
        return "create-budget-item";
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public long createBudgetItem(@RequestBody BudgetItem budgetItem){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        return budgetItemService.createBudgetItem(budgetItem, loggedInUserEmailAddress);
    }

    @RequestMapping(value = "/{budgetItemId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteBudgetItem(@PathVariable long budgetItemId){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetItemService.deleteBudgetItem(budgetItemId, loggedInUserEmailAddress);
    }
}
