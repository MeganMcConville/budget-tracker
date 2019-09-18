package com.megansportfolio.budgettracker.budgetItemUpdate;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budget_item_update")
public class BudgetItemUpdate {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "budget_item_id")
    @ManyToOne(targetEntity = BudgetItem.class)
    private BudgetItem budgetItem;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private int year;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "recurring")
    private boolean isRecurring;

    @Column(name = "month_specific")
    private boolean monthSpecific;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BudgetItem getBudgetItem() {
        return this.budgetItem;
    }

    public void setBudgetItem(BudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isMonthSpecific() {
        return monthSpecific;
    }

    public void setMonthSpecific(boolean monthSpecific) {
        this.monthSpecific = monthSpecific;
    }

    public LocalDate getDate(){
        return LocalDate.of(year, month, 1);
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
}
