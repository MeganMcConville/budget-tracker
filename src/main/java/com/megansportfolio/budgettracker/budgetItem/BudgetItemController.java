package com.megansportfolio.budgettracker.budgetItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/budget-items")
public class BudgetItemController {

    @Autowired
    private BudgetItemService budgetItemService;
}
