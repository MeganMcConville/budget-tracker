package com.megansportfolio.budgettracker;

import com.megansportfolio.budgettracker.budget.BudgetService;
import com.megansportfolio.budgettracker.sharedUser.SharedUserDao;
import com.megansportfolio.budgettracker.sharedUser.SharedUserService;
import com.megansportfolio.budgettracker.user.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModelAttributeControllerAdvice {

    @Autowired
    private SharedUserService sharedUserService;

    @Autowired
    private BudgetService budgetService;

    @ModelAttribute
    public void addAttribues(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
            String loggedInUserEmailAddress = loggedInUser.getUsername();
            model.addAttribute("hasSharedBudgets", sharedUserService.hasSharedBudgets(loggedInUserEmailAddress));
            model.addAttribute("hasBudgets", budgetService.hasBudgets(loggedInUserEmailAddress));
        }
    }

}
