package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.budgetItem.BudgetItem;
import com.megansportfolio.budgettracker.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(targetEntity = User.class)
    private User user;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "budget")
    private List<BudgetItem> budgetItems;

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public User getUser(){
        return this.user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public List<BudgetItem> getBudgetItems(){
        return this.budgetItems;
    }

    public void setBudgetItems(List<BudgetItem> budgetItems){
        this.budgetItems = budgetItems;
    }

}
