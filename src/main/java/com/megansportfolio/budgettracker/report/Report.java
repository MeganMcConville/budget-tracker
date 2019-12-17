package com.megansportfolio.budgettracker.report;

import java.math.BigDecimal;

public class Report {

    private BigDecimal budgetedAmount;

    private BigDecimal amountSpent;

    private BigDecimal percentDifference;

    private BigDecimal averageDifferencePerMonth;

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
}
