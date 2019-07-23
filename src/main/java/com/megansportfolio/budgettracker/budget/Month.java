package com.megansportfolio.budgettracker.budget;

public enum Month {
    JANUARY("January", 1),
    FEBRUARY("February", 2),
    MARCH("March", 3),
    APRIL("April", 4),
    MAY("May", 5),
    JUNE("June", 6),
    JULY("July", 7),
    AUGUST("August", 8),
    SEPTEMBER("September", 9),
    OCTOBER("October", 10),
    NOVEMBER("November", 11),
    DECEMBER("December", 12);

    public final String nameLabel;
    public final int monthNumber;

    public static Month valueOfNameLabel(String nameLabel){
        for(Month i : values()){
            if(i.nameLabel.equals(nameLabel)){
                return i;
            }
        }
        return null;
    }

    public static Month valueOfMonthNumber(int monthNumber){
        for(Month i : values()){
            if(i.monthNumber == monthNumber){
                return i;
            }
        }
        return null;
    }

    public String getNameLabel(){
        return this.nameLabel;
    }

    private Month(String nameLabel, int monthNumber){
        this.nameLabel = nameLabel;
        this.monthNumber = monthNumber;
    }

    public int getMonthNumber(){
        return this.monthNumber;
    }
}
