package com.bhojrajCreation.journalApp.Controller;

import com.bhojrajCreation.journalApp.Entity.JournalEntry;
import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Services.JournalEntryService;
import com.bhojrajCreation.journalApp.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@Tag(name="Journal APIs")
public class JournalApplicationV2 {
    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

        @GetMapping()
        @Operation(summary = "Get All Journal entries of user")
        public ResponseEntity<?> getAll(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username);
            List<JournalEntry> all = user.getJournalEntries();
            if(all!=null && !all.isEmpty()){
                return new ResponseEntity<>(all,HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        @PostMapping()
        @Operation(summary = "Create Journal entry of user")
        public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry){
            try{
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName();
                journalEntryService.saveEntry(entry,username);
                return new ResponseEntity<JournalEntry>(entry, HttpStatus.CREATED);
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        @GetMapping("/id/{myId}")
        @Operation(summary = "Get Journal entry of user by Id")
        public ResponseEntity<JournalEntry> getJournalEntry(@PathVariable String myId) {
            ObjectId newId = new ObjectId(myId);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userService.findByUsername(username);

            List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(newId)).collect(Collectors.toList());

            if (!collect.isEmpty()) {
                Optional<JournalEntry> journalEntry = journalEntryService.findById(newId);
                if (journalEntry.isPresent()) {
                    return new ResponseEntity<JournalEntry>(journalEntry.get(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        @DeleteMapping("/id/{myId}")
        @Operation(summary = "Delete Journal entry of user by Id")
        public ResponseEntity<?> deleteJournalEntry(@PathVariable ObjectId myId){

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName();
                boolean removed = journalEntryService.deleteById(myId,username);
                if(removed){
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }else{
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }




        }

        @PutMapping("/id/{myId}")
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

}
