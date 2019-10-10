package com.megansportfolio.budgettracker;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetTrackerConfig {

    @Bean
    public EmailValidator validator(){
        EmailValidator validator = EmailValidator.getInstance();
        return validator;
    }

}
