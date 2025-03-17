package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Services.UserDetailsServiceImp;
import com.bhojrajCreation.journalApp.Services.UserService;
import com.bhojrajCreation.journalApp.Utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class PubilcController {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserDetailsServiceImp userDetailsService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @GetMapping("health-check")
    public String healthCheck(){
        return "Ok";
    }


    @PostMapping("/sign-up")
    public void signUp(@RequestBody User user){
        userService.saveNewUser(user);
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> logIn(@RequestBody User user){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);

        }catch (Exception e){
            log.error("Exception occured while create Authentication Token ",e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

    }
}
