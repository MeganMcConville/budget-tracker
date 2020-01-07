package com.megansportfolio.budgettracker;

import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class BudgetTrackerConfig {

    @Bean
    public EmailValidator validator(){
        EmailValidator validator = EmailValidator.getInstance();
        return validator;
    }

    @Bean
    public freemarker.template.Configuration configuration(){
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(new Version("2.3.29"));
        configuration.setClassForTemplateLoading(BudgetTrackerConfig.class, "/ftl-templates");

        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.US);

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        return configuration;
    }

}
