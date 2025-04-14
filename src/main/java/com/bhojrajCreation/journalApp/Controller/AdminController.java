package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.Cache.AppCache;
import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name="Admin APIs")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private AppCache appCache;

    @GetMapping("/all-users")
    @Operation(summary = "Get All users")
    public ResponseEntity<?> getAllUsers(){
        List<User> all = userService.getAll();
        if(all!=null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-user-admin")
    @Operation(summary = "Create Admin user")
    public void createUser(@RequestBody User user){
        userService.saveAdmin(user);
    }

    @GetMapping("/clear-app-cache")
    @Operation(summary = "Clear App Cache")
    public void clearAppCache(){
        appCache.init();
    }
}
