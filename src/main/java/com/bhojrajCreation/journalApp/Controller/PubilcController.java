//package com.bhojrajCreation.journalApp.Controller;
//
//import com.bhojrajCreation.journalApp.DTO.UserDto;
//import com.bhojrajCreation.journalApp.Entity.User;
//import com.bhojrajCreation.journalApp.Services.UserDetailsServiceImp;
//import com.bhojrajCreation.journalApp.Services.UserService;
//import com.bhojrajCreation.journalApp.Utils.JwtUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/public")
//@Slf4j
//@Tag(name="Public APIs")
//public class PubilcController {
//
//    @Autowired
//    JwtUtil jwtUtil;
//    @Autowired
//    UserDetailsServiceImp userDetailsService;
//    @Autowired
//    AuthenticationManager authenticationManager;
//    @Autowired
//    private UserService userService;
//    @GetMapping("health-check")
//    @Operation(summary = "Health Check")
//    public String healthCheck(){
//        return "Ok";
//    }
//
//
//    @PostMapping("/sign-up")
//    @Operation(summary = "Sign-up")
//    public void signUp(@RequestBody UserDto user){
//         User newUser = new User();
//         newUser.setEmail(user.getEmail());
//         newUser.setUsername(user.getUsername());
//         newUser.setPassword(user.getPassword());
//         //newUser.setSentimentAnalysis(user.isSentimentAnalysis());
//        userService.saveNewUser(newUser);
//    }
//
//    @PostMapping("/log-in")
//    @Operation(summary = "Log-In")
//    public ResponseEntity<?> logIn(@RequestBody User user){
//        try{
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken   (user.getUsername(),user.getPassword()));
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//            String jwt = jwtUtil.generateToken(userDetails.getUsername());
//            return new ResponseEntity<>(jwt, HttpStatus.OK);
//
//        }catch (Exception e){
//            log.error("Exception occured while create Authentication Token ",e);
//            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
//        }
//
//    }
//
//
//}
