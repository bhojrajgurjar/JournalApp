package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.ApiResponse.QuoteResponse;
import com.bhojrajCreation.journalApp.ApiResponse.WeatherResponse;
import com.bhojrajCreation.journalApp.DTO.UserDto;
import com.bhojrajCreation.journalApp.Entity.JournalEntry;
import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Repository.UserRepository;
import com.bhojrajCreation.journalApp.Services.*;
import com.bhojrajCreation.journalApp.Utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@Tag(name="User APIs")
@Slf4j
public class UserController {
    private final String API_URL = "http://localhost:8080/"; // Replace with your backend URL
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private QuotesService quotesService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    private HttpServletRequest request;


    // Show signup form
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "signup"; // Make sure this template exists
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute("user") UserDto userDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            if(userDto.getPassword().length()<6){
                redirectAttributes.addFlashAttribute("error", "Password must of 6 character atleast");
                return "redirect:/user/signup";
            }
            redirectAttributes.addFlashAttribute("error", "Sign Up Failed");
            return "redirect:/user/signup";
        }

        if (userService.findByUsername(userDto.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/user/signup";
        }

        if (userService.findByEmail(userDto.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/user/signup";
        }


        try {
            User newUser = new User();
            newUser.setUsername(userDto.getUsername());
            newUser.setEmail(userDto.getEmail());
            newUser.setPassword(userDto.getPassword()); // TODO: encrypt password
            newUser.setRoles(Arrays.asList("USER"));

            userService.saveNewUser(newUser);

            redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
            return "redirect:/user/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Signup failed. Try again.");
            return "redirect:/user/signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "login";
    }

    @PostMapping("/login")
    public String  loginUser(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            // 1. Authenticate the user
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // 2. If authentication is successful
            if (auth.isAuthenticated()) {
                // 3. Load user details
                UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(user.getUsername());

                // 4. Generate JWT token
                String jwt = jwtUtil.generateToken(userDetails.getUsername());

                // 5. Set JWT in HTTP-only cookie
                Cookie oldCookie = new Cookie("jwt", null);
                oldCookie.setMaxAge(0); // Expire immediately
                oldCookie.setPath("/");
                response.addCookie(oldCookie);

// Add new cookie
                Cookie newCookie = new Cookie("jwt", jwt);
                newCookie.setHttpOnly(true);
                newCookie.setPath("/");
                newCookie.setMaxAge(24 * 60 * 60);

                response.addCookie(newCookie);

                // 6. Redirect to user home
                return "redirect:/user/home";
            }

        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            log.error("Login failed", e);
            return "redirect:/user/login";
        }

        return "login";
    }



//


    @GetMapping("/home")
    public String showUserHome(Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/user/login"; // user not logged in
        }

        String username = authentication.getName(); // works with JWT
        System.out.println("Username from context: " + username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/user/login"; // user not found
        }

        model.addAttribute("username", user.getUsername());

        // Show latest journal entry (if any)
        List<JournalEntry> entries = user.getJournalEntries();
        if (entries != null && !entries.isEmpty()) {

            JournalEntry latestEntry = entries.get(entries.size() - 1);
            model.addAttribute("latestEntry", latestEntry);
        }
        model.addAttribute("request", request);
        return "user-home";
    }

    @GetMapping("/add-entry")
    public String showAddJournalPage(Model model) {
        model.addAttribute("journalEntry", new JournalEntry());
        return "add-journal";
    }

    @PostMapping("/save-entry")
    public String saveJournalEntry(@ModelAttribute JournalEntry journalEntry) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        journalEntryService.saveEntry(journalEntry, username);
        return "redirect:/user/home";
    }

    @GetMapping("/view-journal")
    public String viewAllEntries(Model model,HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> entries = user.getJournalEntries();
        model.addAttribute("entries", entries);
        model.addAttribute("request", request);
        return "view-journal";
    }


//    @DeleteMapping("/journal/delete/{id}")
//    @Operation(summary = "Delete Journal entry of user by Id")
//    public ResponseEntity<Void> deleteJournalEntry(@PathVariable("id") ObjectId id) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String username = auth.getName();
//
//        boolean removed = journalEntryService.deleteById(id, username);
//
//        if (removed) {
//            return ResponseEntity.noContent().build(); // ✅ returns 204
//        } else {
//            return ResponseEntity.notFound().build();  // ✅ returns 404
//        }
//    }




    @PostMapping("/journal/delete")
    @Operation(summary = "Delete Journal entry of user by Id")
    public String deleteJournalEntry(@RequestParam("id") String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean removed = journalEntryService.deleteById(new ObjectId(id), username);

        // Fallback to the default redirect if no redirectTo is provided
        if (removed) {
            return "redirect:/user/view-journal";
        } else {
            return "redirect:/user/view-journal?error=notfound";
        }
    }

    @PostMapping("/home/journal/delete")
    @Operation(summary = "Delete Journal entry of user by Id")
    public String deleteJournalEntryFromHome(@RequestParam("id") String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean removed = journalEntryService.deleteById(new ObjectId(id), username);

        // Fallback to the default redirect if no redirectTo is provided
        if (removed) {
            return "redirect:/user/home";
        } else {
            return "redirect:/user/home?error=notfound";
        }
    }





    @GetMapping("/edit-journal/{id}")
    public String editJournal(@PathVariable ObjectId id, Model model) {
        Optional<JournalEntry> entryOptional = journalEntryService.findById(id); // load from DB
        if (entryOptional.isPresent()) {
            JournalEntry entry = entryOptional.get();
            model.addAttribute("entry", entry);
            return "edit-journal";
        } else {
            // Handle the case where the journal entry is not found.
            // You can return a custom error page or redirect.
            // For example, redirect to the home page or a not-found page.
            return "redirect:/user/home"; // Or "redirect:/404"
        }
    }
    @PutMapping("/update/id/{myId}")
    @Operation(summary = "Update Journal entry of user by Id")
    public ResponseEntity<JournalEntry> updateJournalEntry(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username);

        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());

        if (!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            if (journalEntry.isPresent()) {
                JournalEntry old = journalEntry.get();
                if(old!=null){
                    old.setTittle(newEntry.getTittle() != null && newEntry.getTittle() != "" ? newEntry.getTittle() : old.getTittle());
                    old.setContent(newEntry.getContent() != null && newEntry.getContent() != "" ? newEntry.getContent() : old.getContent());
                    journalEntryService.saveEntry(old);
                    return new ResponseEntity<>(old,HttpStatus.OK);
                }
            }
        }



        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    //    @GetMapping
//    public List<User> getAllUser(){
//        return userService.getAll();
//    }
    @PutMapping()
    @Operation(summary = "Update user")
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
    @Operation(summary = "Delete User by Id")
    public ResponseEntity<?> deleteUserById(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUsername(auth.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get Greetings")
    public ResponseEntity<?> greeting(@PathVariable String city){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        WeatherResponse weatherResponse = weatherService.getWeather(city);
        List<QuoteResponse> quoteResponse = quotesService.quoteForYou();

       String greet="";
        if(weatherResponse!=null){
            greet=", Weather at "+city+" feels like " + weatherResponse.getCurrent().getFeelslike();
        }

        return new ResponseEntity<>("Hi  "+auth.getName()+ greet +" and quote for you is ' "+quoteResponse.get(0).getQuote()+" '" , HttpStatus.OK);
    }

}

/*
*package com.example.journalapp.controller;

import com.example.journalapp.model.JournalEntry;
import com.example.journalapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {





    // Welcome Page


    // Signup Page


    // Process Signup


    // Login Page


    // Process Login


    // User Home Page


    // Add Journal Entry Form
    @GetMapping("/add-journal")
    public String showAddJournalForm(Model model) {
        model.addAttribute("entry", new JournalEntry());
        return "add-journal";
    }

    // Save Journal Entry
    @PostMapping("/save-journal")
    public String saveJournalEntry(@ModelAttribute JournalEntry entry) {
        restTemplate.postForObject(API_URL + "/entries", entry, JournalEntry.class);
        return "redirect:/user/home";
    }

    // Edit Journal Entry Form
    @GetMapping("/edit-journal/{id}")
    public String showEditJournalForm(@PathVariable Long id, Model model) {
        JournalEntry entry = restTemplate.getForObject(API_URL + "/entries/" + id, JournalEntry.class);
        model.addAttribute("entry", entry);
        return "edit-journal";
    }

    // Update Journal Entry
    @PostMapping("/update-journal/{id}")
    public String updateJournalEntry(@PathVariable Long id, @ModelAttribute JournalEntry entry) {
        restTemplate.put(API_URL + "/entries/" + id, entry);
        return "redirect:/user/home";
    }

    // Delete Journal Entry
    @GetMapping("/delete-journal/{id}")
    public String deleteJournalEntry(@PathVariable Long id) {
        restTemplate.delete(API_URL + "/entries/" + id);
        return "redirect:/user/home";
    }

    // Logout
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/user/welcome";
    }
}
* */

