package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.ApiResponse.QuoteResponse;
import com.bhojrajCreation.journalApp.ApiResponse.WeatherResponse;
import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Repository.UserRepository;
import com.bhojrajCreation.journalApp.Services.QuotesService;
import com.bhojrajCreation.journalApp.Services.UserService;
import com.bhojrajCreation.journalApp.Services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private QuotesService quotesService;
//
//    @GetMapping
//    public List<User> getAllUser(){
//        return userService.getAll();
//    }
    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User userInDb = userService.findByUsername(username);

        if(userInDb!=null){
            userInDb.setUsername(user.getUsername());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(auth.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping()
    public ResponseEntity<?> greeting(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        WeatherResponse weatherResponse = weatherService.getWeather("Pune");
        List<QuoteResponse> quoteResponse = quotesService.quoteForYou();

       String greet="";
        if(weatherResponse!=null){
            greet=", Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }

        return new ResponseEntity<>("Hi  "+auth.getName()+ greet +" and quote for you is ' "+quoteResponse.get(0).getQuote()+" '" , HttpStatus.OK);
    }

}

