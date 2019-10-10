package com.megansportfolio.budgettracker;

import com.megansportfolio.budgettracker.sharedUser.EmailIsCurrentUserException;
import com.megansportfolio.budgettracker.user.InvalidEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView exception(HttpServletRequest request, Exception exception){
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("error");

        return modelAndView;
    }

    @ExceptionHandler(value = InvalidEmailException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String invalidEmailException(HttpServletRequest request, Exception exception){
        return "invalid email";
    }

    @ExceptionHandler(value = EmailIsCurrentUserException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String emailIsCurrentUserException(HttpServletRequest request, Exception exception){
        return "email is current user";
    }

}
