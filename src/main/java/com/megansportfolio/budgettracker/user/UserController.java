package com.megansportfolio.budgettracker.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String getCreateUser(Model model){
        model.addAttribute("user", new User());
        return "create-user";
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String createUser(@ModelAttribute User user){

        try {
            userService.createUser(user);
        }
        catch (EmailExistsException e){
            return "redirect:/users?exists";
        }
        catch (InvalidEmailException e){
            return "redirect:/users?invalid";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String viewAccount(Model model){
        UserDetails loggedInUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserUsername = loggedInUser.getUsername();
        User currentUser = userService.getUser(loggedInUserUsername);
        model.addAttribute("currentUser", currentUser);
        return "account";
    }

}
