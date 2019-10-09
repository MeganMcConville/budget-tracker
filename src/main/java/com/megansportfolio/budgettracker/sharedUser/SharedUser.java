package com.megansportfolio.budgettracker.sharedUser;

import com.megansportfolio.budgettracker.budget.Budget;
import com.megansportfolio.budgettracker.user.User;

import javax.persistence.*;

@Entity
@Table(name = "shared_user")
public class SharedUser {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(targetEntity = User.class)
    private User user;

    @JoinColumn(name = "budget_id")
    @ManyToOne(targetEntity = Budget.class)
    private Budget budget;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
}
