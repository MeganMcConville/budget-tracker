package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;
import com.megansportfolio.budgettracker.budgetItemUpdate.BudgetItemUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "budget_item")
public class BudgetItem {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "budget_id")
    @ManyToOne(targetEntity = Budget.class)
    private Budget budget;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BudgetItemType budgetItemType;

    @OneToMany(mappedBy = "budgetItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetEntry> budgetEntries;

    @OneToMany(mappedBy = "budgetItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetItemUpdate> budgetItemUpdates;

    @Column(name = "recurring")
    private boolean isRecurring;

    @Transient
    private BigDecimal totalSpent;

    @Transient
    private BigDecimal totalRemaining;

    @Transient
    private BigDecimal yearlyBudgetedAmount;

    @Transient
    private BigDecimal yearlyAmountSpent;

    @Transient
    private BigDecimal percentDifference;

    @Transient
    private BigDecimal averageMonthlyDifference;

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public Budget getBudget(){
        return this.budget;
    }

    public void setBudget(Budget budget){
        this.budget = budget;
    }

    public BigDecimal getAmount(){
        return this.amount;
    }

    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public BudgetItemType getBudgetItemType(){
        return this.budgetItemType;
    }

    public void setBudgetItemType(BudgetItemType budgetItemType){
        this.budgetItemType = budgetItemType;
    }

    public List<BudgetEntry> getBudgetEntries() {
        return this.budgetEntries;
    }

    public void setBudgetEntries(List<BudgetEntry> budgetEntries) {
        this.budgetEntries = budgetEntries;
    }

    public BigDecimal getTotalSpent() {
        return this.totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getTotalRemaining() {
        return this.totalRemaining;
    }

    public void setTotalRemaining(BigDecimal totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public List<BudgetItemUpdate> getBudgetItemUpdates() {
        return this.budgetItemUpdates;
    }

    public void setBudgetItemUpdates(List<BudgetItemUpdate> budgetItemUpdates) {
        this.budgetItemUpdates = budgetItemUpdates;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public BigDecimal getYearlyBudgetedAmount() {
        return this.yearlyBudgetedAmount;
    }

    public void setYearlyBudgetedAmount(BigDecimal yearlyBudgetedAmount) {
        this.yearlyBudgetedAmount = yearlyBudgetedAmount;
    }

    public BigDecimal getYearlyAmountSpent() {
        return this.yearlyAmountSpent;
    }

    public void setYearlyAmountSpent(BigDecimal yearlyAmountSpent) {
        this.yearlyAmountSpent = yearlyAmountSpent;
    }

    public BigDecimal getPercentDifference() {
        return this.percentDifference;
    }

    public void setPercentDifference(BigDecimal percentDifference) {
        this.percentDifference = percentDifference;
    }

    public BigDecimal getAverageMonthlyDifference() {
        return this.averageMonthlyDifference;
    }

    public void setAverageMonthlyDifference(BigDecimal averageMonthlyDifference) {
        this.averageMonthlyDifference = averageMonthlyDifference;
    }
}
