package com.megansportfolio.budgettracker.budgetItem;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.budgetEntry.BudgetEntry;

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

    @OneToMany(mappedBy = "budgetItemId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetEntry> budgetEntries;

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
}
