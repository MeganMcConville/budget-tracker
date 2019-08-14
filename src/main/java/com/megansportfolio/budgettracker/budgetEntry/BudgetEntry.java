package com.megansportfolio.budgettracker.budgetEntry;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budget_entry")
public class BudgetEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "budget_item_id")
    @ManyToOne(targetEntity = BudgetItem.class)
    private BudgetItem budgetItem;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "month")
    private BudgetEntryMonth budgetEntryMonth;

    @Column(name = "year")
    private long year;

    @Column(name = "notes")
    private String notes;

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

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BudgetEntryMonth getBudgetEntryMonth() {
        return this.budgetEntryMonth;
    }

    public void setBudgetEntryMonth(BudgetEntryMonth budgetEntryMonth) {
        this.budgetEntryMonth = budgetEntryMonth;
    }

    public long getYear() {
        return this.year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}