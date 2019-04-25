package com.megansportfolio.budgettracker.budget;

import com.megansportfolio.budgettracker.user.User;

import javax.persistence.*;

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

}
