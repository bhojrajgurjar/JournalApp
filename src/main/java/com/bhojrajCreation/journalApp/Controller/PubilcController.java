package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PubilcController {
    @Autowired
    private UserService userService;
    @GetMapping("health-check")
    public String healthCheck(){
        return "Ok";
    }


    @PostMapping("/create-user")
    public void createUser(@RequestBody User user){
        userService.saveNewUser(user);
    }
}
