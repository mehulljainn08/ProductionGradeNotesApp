package org.example.newjournal.controller;

import org.example.newjournal.Service.WeatherService;
import org.example.newjournal.api.WeatherResponse;
import org.example.newjournal.entity.User;
import org.example.newjournal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping
    public ResponseEntity<?> greeting() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);
        try {
            ResponseEntity<?> response = weatherService.getWeather(user.getCity());
            if (response.getBody() instanceof WeatherResponse weatherResponse) {
                return ResponseEntity.ok("Welcome, " + userName + "! Weather feels like " + weatherResponse.getCurrent().getFeelslike() + "°C in " + user.getCity());
            } else {
                return ResponseEntity.ok("Welcome, " + userName);
            }
        } catch (Exception e) {
            return ResponseEntity.ok("Welcome, " + userName);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();
            if (!currentUserName.equals(user.getUserName())) {
                return ResponseEntity.status(403).body("You can only update your own user details");
            }
            User existingUser=userRepository.findByUserName(currentUserName);
            if (existingUser == null) {
                return ResponseEntity.status(404).body("User not found");
            }
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(existingUser);
            return new ResponseEntity<>("User updated successfully!", HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user: " + e.getMessage());
        } 

    }

    @DeleteMapping
    public ResponseEntity<?> DeleteUser() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User oldUser=userRepository.findByUserName(userName);
        if(oldUser!=null){
            userRepository.delete(oldUser);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
