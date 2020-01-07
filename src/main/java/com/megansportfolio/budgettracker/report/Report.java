package com.megansportfolio.budgettracker.report;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;

import java.math.BigDecimal;
import java.util.List;

public class Report {

    private BigDecimal budgetedAmount;

    private BigDecimal amountSpent;

    private BigDecimal percentDifference;

    private BigDecimal averageDifferencePerMonth;

    private List<BudgetItem> budgetItems;

    public BigDecimal getBudgetedAmount() {
        return this.budgetedAmount;
    }

    public void setBudgetedAmount(BigDecimal budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public BigDecimal getAmountSpent() {
        return this.amountSpent;
    }

    public void setAmountSpent(BigDecimal amountSpent) {
        this.amountSpent = amountSpent;
    }

    public BigDecimal getPercentDifference() {
        return this.percentDifference;
    }

    public void setPercentDifference(BigDecimal percentDifference) {
        this.percentDifference = percentDifference;
    }

    public BigDecimal getAverageDifferencePerMonth() {
        return this.averageDifferencePerMonth;
    }

    public void setAverageDifferencePerMonth(BigDecimal averageDifferencePerMonth) {
        this.averageDifferencePerMonth = averageDifferencePerMonth;
    }

    public List<BudgetItem> getBudgetItems(){
        return this.budgetItems;
    }

    public void setBudgetItems(List<BudgetItem> budgetItems){
        this.budgetItems = budgetItems;
    }
}
