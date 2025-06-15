package org.example.newjournal.controller;

import java.util.Arrays;

import org.example.newjournal.entity.User;
import org.example.newjournal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user) {

        // Check if the user is authenticated and has the ROLE_ADMIN authority
        try {
            if (user == null || user.getUserName() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Invalid admin user data provided.");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication==null) {
                return ResponseEntity.status(403).body("You must be authenticated to create an admin user.");
            }
            if(authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).body("You do not have permission to create an admin user.");
           }
            user.setRoles(Arrays.asList("ROLE_ADMIN"));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            return ResponseEntity.ok("Admin user created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating admin user: " + e.getMessage());
        }
        
    }
    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "Access denied. You must be an admin to view this dashboard.";
        }
        String adminUserName = authentication.getName();
        return "Welcome to the Admin Dashboard "+adminUserName + "!";
    }
    @GetMapping("/get-users")
    public ResponseEntity<?> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("Access denied. You must be an admin to view users.");
        }
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving users: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("Access denied. You must be an admin to delete users.");
        }
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return ResponseEntity.status(404).body("User not found.");
            }
            userRepository.delete(existingUser);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting user: " + e.getMessage());
        }
    }
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("Access denied. You must be an admin to update users.");
        }
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return ResponseEntity.status(404).body("User not found.");
            }
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(existingUser);
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user: " + e.getMessage());
        }
    }


}
