package org.example.newjournal.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.example.newjournal.entity.JournalEntry;
import org.example.newjournal.entity.User;
import org.example.newjournal.repository.JournalEntryRepository;
import org.example.newjournal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/journal-entries")
    public ResponseEntity<?> getUserEntries(){
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            User user=userRepository.findByUserName(currentUserName);
            if (user == null) {
                return new ResponseEntity<>("User not found", org.springframework.http.HttpStatus.NOT_FOUND);
            }
            List<JournalEntry> entries= user.getJournalEntries();
            return new ResponseEntity<>(entries, org.springframework.http.HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving journal entries: " + e.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping
    public ResponseEntity<?> createJournalEntry(@RequestBody JournalEntry journalEntry){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User user=userRepository.findByUserName(userName);
        if(user==null){
            return new ResponseEntity<>("User with such userName does not exist",HttpStatus.NOT_FOUND);
        }
        journalEntry.setDate(LocalDateTime.now());

        journalEntryRepository.save(journalEntry);


        user.getJournalEntries().add(journalEntry);
        userRepository.save(user);

        return new ResponseEntity<>("Entry Created",HttpStatus.CREATED);
    }


    @GetMapping("id/{id}")
    public ResponseEntity<?> getEntryById(@PathVariable("id") String id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();


        User user = userRepository.findByUserName(username);
        if (user == null) {
            return new ResponseEntity<>("user does not exist",HttpStatus.NOT_FOUND);
        }


        Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
        if (optionalEntry.isPresent() && user.getJournalEntries().contains(optionalEntry.get())) {
            return new ResponseEntity<>(optionalEntry.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deleteEntrybyId(@PathVariable String id){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User user=userRepository.findByUserName(userName);
        if (!ObjectId.isValid(id)) {
            return new ResponseEntity<>("Invalid ID format", HttpStatus.BAD_REQUEST);
        }
        if(user==null){
            return new ResponseEntity<>("User with such userName does not exist",HttpStatus.NOT_FOUND);
        }

        Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
        List<JournalEntry> userEntries=user.getJournalEntries();
        if(optionalEntry.isPresent() && userEntries.contains(optionalEntry.get())){
            userEntries.remove(optionalEntry.get());
            userRepository.save(user);
            return new ResponseEntity<>("Entry Deleted",HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("id/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable String id, @RequestBody JournalEntry journalEntry) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return new ResponseEntity<>("User with such userName does not exist", HttpStatus.NOT_FOUND);
        }
        if (optionalEntry.isEmpty()) {
            return new ResponseEntity<>("Entry with given ID does not exist", HttpStatus.NOT_FOUND);
        }
        List<JournalEntry> userEntries=user.getJournalEntries();
        JournalEntry existingEntry = optionalEntry.get();
        if(userEntries.contains(existingEntry)){
            existingEntry.setTitle(journalEntry.getTitle());
            existingEntry.setContent(journalEntry.getContent());
            journalEntryRepository.save(existingEntry);
            return new ResponseEntity<>("Journal entry updated successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Entry with given ID does not exist", HttpStatus.NOT_FOUND);

    }
}
