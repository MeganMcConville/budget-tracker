package com.megansportfolio.budgettracker.surroundingDates;

import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class SurroundingDatesService {

    public SurroundingDates getSurroundingDates(Integer currentMonth, Integer currentYear){

        SurroundingDates surroundingDates = new SurroundingDates();
        if(currentMonth == null || currentYear == null){
            Calendar cal = Calendar.getInstance();
            currentMonth = (cal.get(Calendar.MONTH)) + 1;
            currentYear = (cal.get(Calendar.YEAR));
        }

        surroundingDates.setPreviousMonth(currentMonth - 1);
        surroundingDates.setPreviousYear(currentYear);
        surroundingDates.setNextMonth(currentMonth + 1);
        surroundingDates.setNextYear(currentYear);

        if(currentMonth == 12){
            surroundingDates.setNextMonth(1);
            surroundingDates.setNextYear(currentYear + 1);
        }
        if(currentMonth == 1){
            surroundingDates.setPreviousMonth(12);
            surroundingDates.setPreviousYear(currentYear - 1);
        }

        return surroundingDates;
    }

}
