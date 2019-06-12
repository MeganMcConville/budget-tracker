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
    public void createBudgetItem(@RequestBody BudgetItem budgetItem){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetItemService.createBudgetItem(budgetItem, loggedInUserEmailAddress);
    }

    @RequestMapping(method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateBudgetItems(@RequestBody List<BudgetItem> budgetItems){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserEmailAddress = loggedInUser.getUsername();
        budgetItemService.updateBudgetItems(budgetItems, loggedInUserEmailAddress);
    }
}
