package com.megansportfolio.budgettracker.budgetItem;

public enum BudgetItemType {
    ANNUAL("Annual"),
    MONTHLY("Monthly");

    public final String lowerCaseLabel;

    public static BudgetItemType valueOfLowerCaseLabel(String lowerCaseLabel){
        for(BudgetItemType i : values()){
            if(i.lowerCaseLabel.equals(lowerCaseLabel)){
                return i;
            }
        }
        return null;
    }

    BudgetItemType(String lowerCaseLabel){
        this.lowerCaseLabel = lowerCaseLabel;
    }

    public String getLowerCaseLabel(){
        return this.lowerCaseLabel;
    }
}
