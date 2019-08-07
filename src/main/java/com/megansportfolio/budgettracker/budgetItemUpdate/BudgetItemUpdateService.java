package com.megansportfolio.budgettracker.budgetItemUpdate;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetItemUpdateService {

    @Autowired
    private BudgetItemUpdateDao budgetItemUpdateDao;

    @Autowired
    private BudgetItemDao budgetItemDao;

    @Transactional
    public void createBudgetItemUpdates(List<BudgetItemUpdate> budgetItemUpdates, String loggedInUserEmailAddress){
        List<Long> ids = budgetItemUpdates.stream().map(x -> x.getBudgetItem().getId()).collect(Collectors.toList());
        List<BudgetItem> originalBudgetItems = budgetItemDao.findAllById(ids);
        List<BudgetItemUpdate> updatesToSave = new ArrayList<>();
        for(BudgetItemUpdate budgetItemUpdate : budgetItemUpdates){
            BudgetItem correspondingBudgetItem = originalBudgetItems.stream()
                    .filter(x -> x.getId() == budgetItemUpdate.getBudgetItem().getId())
                    .findFirst().get();
            if(!correspondingBudgetItem.getBudget().getUser().getUsername().equals(loggedInUserEmailAddress)){
                throw new RuntimeException();
            }

            else{
                budgetItemUpdate.setBudgetItem(correspondingBudgetItem);
                long budgetItemId = budgetItemUpdate.getBudgetItem().getId();
                int updateMonth = budgetItemUpdate.getMonth();
                int updateYear = budgetItemUpdate.getYear();
                BudgetItemUpdate existingUpdate = budgetItemUpdateDao.findOneByBudgetItemIdAndMonthAndYear(budgetItemId, updateMonth, updateYear);
                if(existingUpdate != null){
                    budgetItemUpdateDao.delete(existingUpdate);
                }
                updatesToSave.add(budgetItemUpdate);
            }

        }

        budgetItemUpdateDao.saveAll(updatesToSave);
    }

}
