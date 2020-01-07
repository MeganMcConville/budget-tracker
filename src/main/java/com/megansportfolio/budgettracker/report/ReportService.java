package com.megansportfolio.budgettracker.report;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budget.BudgetDao;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntryDao;
import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.budgetItem.BudgetItemType;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateDao;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    BudgetItemUpdateDao budgetItemUpdateDao;

    @Autowired
    BudgetDao budgetDao;

    @Autowired
    BudgetEntryDao budgetEntryDao;

    //set all values not in the controller
    public List<BudgetItem> getReportBudgetItems(long budgetId, int year, String loggedInUserEmailAddress){
        Optional<Budget> budget = budgetDao.findById(budgetId);
        if(budget.isPresent()){
            if(!budget.get().getUser().getUsername().equals(loggedInUserEmailAddress)){
                throw new RuntimeException();
            }
            else {
                List<BudgetItem> budgetItems = budget.get().getBudgetItems();
                for (BudgetItem budgetItem : budgetItems) {
                    setYearlyBudgetedAmount(budgetItem, year);
                    setYearlyAmountSpent(budgetItem, year);
                    setPercentDifference(budgetItem);
                    setAverageMonthlyDifference(budgetItem);
                }
                return budgetItems;
            }
        }
        else{
            return null;
        }
    }

    //get with updates yearly budgeted, BI
    public void setYearlyBudgetedAmount(BudgetItem budgetItem, int year){
        BigDecimal yearlyBudgetedAmount = BigDecimal.ZERO;
        List<BudgetItemUpdate> budgetItemUpdates = budgetItemUpdateDao.findAllByBudgetItemIdAndYear(budgetItem.getId(), year);

        if(budgetItem.getBudgetItemType() == BudgetItemType.ANNUAL){
            if(budgetItemUpdates.isEmpty()){
                yearlyBudgetedAmount = budgetItem.getAmount();
            }
            else{
                List<BudgetItemUpdate> sortedUpdates = budgetItemUpdates.stream()
                        .sorted(Comparator.comparing(BudgetItemUpdate::getDate).reversed())
                        .collect(Collectors.toList());
                yearlyBudgetedAmount = sortedUpdates.get(0).getAmount();
            }
        }
        else {
            for(int i = 1; i <= 12; i++){
                Optional<BudgetItemUpdate> correspondingUpdate = BudgetItemUpdateService.findCorrespondingBudgetItemUpdate(year, i, budgetItemUpdates);
                if(correspondingUpdate.isPresent()){
                    yearlyBudgetedAmount = yearlyBudgetedAmount.add(correspondingUpdate.get().getAmount());
                }
                else{
                    yearlyBudgetedAmount = yearlyBudgetedAmount.add(budgetItem.getAmount());
                }
            }
        }
        budgetItem.setYearlyBudgetedAmount(yearlyBudgetedAmount);
    }

    //get yearly spent, BI
    public void setYearlyAmountSpent(BudgetItem budgetItem, int year){
        BigDecimal yearlyAmountSpent = BigDecimal.ZERO;
        List<BudgetEntry> budgetEntries = budgetEntryDao.findAllByBudgetItemIdAndYear(budgetItem.getId(), year);
        for(BudgetEntry budgetEntry : budgetEntries){
            yearlyAmountSpent = yearlyAmountSpent.add(budgetEntry.getAmount());
        }
        budgetItem.setYearlyAmountSpent(yearlyAmountSpent);
    }

    //calculate  percent difference between two, BI
    public void setPercentDifference(BudgetItem budgetItem){
        if(budgetItem.getYearlyBudgetedAmount().compareTo(BigDecimal.ZERO) == 0){
            budgetItem.setPercentDifference(BigDecimal.ZERO);
        }
        else {
            BigDecimal difference = budgetItem.getYearlyAmountSpent().subtract(budgetItem.getYearlyBudgetedAmount());
            BigDecimal percentDifference = difference.divide(budgetItem.getYearlyBudgetedAmount(), 2, RoundingMode.HALF_UP);
            percentDifference = percentDifference.multiply(BigDecimal.valueOf(100));
            budgetItem.setPercentDifference(percentDifference);
        }
    }

    //calculate average over/under per month, BI
    public void setAverageMonthlyDifference(BudgetItem budgetItem){
        BigDecimal difference = budgetItem.getYearlyAmountSpent().subtract(budgetItem.getYearlyBudgetedAmount());
        budgetItem.setAverageMonthlyDifference(difference.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
    }

    public Report generateReport(long budgetId, String loggedInUserEmailAddress, int year){
        Report report = new Report();
        report.setBudgetItems(getReportBudgetItems(budgetId, year, loggedInUserEmailAddress));
        setTotalYearlyBudgetedAmount(report, report.getBudgetItems());
        setTotalYearlyAmountSpent(report, report.getBudgetItems());
        setTotalPercentDifference(report);
        setTotalMonthlyDifference(report);
        return report;
    }

    //get total of all yearly budgeted, Report
    public void setTotalYearlyBudgetedAmount(Report report, List<BudgetItem> budgetItems){
        BigDecimal totalYearlyBudgetedAmount = BigDecimal.ZERO;
        for(BudgetItem budgetItem : budgetItems){
            totalYearlyBudgetedAmount = totalYearlyBudgetedAmount.add(budgetItem.getYearlyBudgetedAmount());
        }
        report.setBudgetedAmount(totalYearlyBudgetedAmount);
    }

    //get total of all yearly spent, Report
    public void setTotalYearlyAmountSpent(Report report, List<BudgetItem> budgetItems){
        BigDecimal totalYearlyAmountSpent = BigDecimal.ZERO;
        for(BudgetItem budgetItem : budgetItems){
            totalYearlyAmountSpent = totalYearlyAmountSpent.add(budgetItem.getYearlyAmountSpent());
        }
        report.setAmountSpent(totalYearlyAmountSpent);
    }

    //total percent difference, Report
    public void setTotalPercentDifference(Report report){
        if(report.getBudgetedAmount().compareTo(BigDecimal.ZERO) == 0){
            report.setPercentDifference(BigDecimal.ZERO);
        }
        else {
            BigDecimal totalDifference = report.getAmountSpent().subtract(report.getBudgetedAmount());
            BigDecimal totalPercentDifference = totalDifference.divide(report.getBudgetedAmount(), 2, RoundingMode.HALF_UP);
            totalPercentDifference = totalPercentDifference.multiply(BigDecimal.valueOf(100));
            report.setPercentDifference(totalPercentDifference);
        }
    }

    //total difference over/under per month, Report
    public void setTotalMonthlyDifference(Report report){
        BigDecimal difference = report.getAmountSpent().subtract(report.getBudgetedAmount());
        report.setAverageDifferencePerMonth(difference.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
    }

}
