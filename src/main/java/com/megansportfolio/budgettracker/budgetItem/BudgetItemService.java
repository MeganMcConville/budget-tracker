package com.megansportfolio.budgettracker.budgetItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetItemService {

    @Autowired
    private BudgetItemDao budgetItemDao;
}
