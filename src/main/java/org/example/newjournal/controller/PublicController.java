package org.example.newjournal.controller;

import java.util.Arrays;

import org.example.newjournal.Service.UserDetailsServiceImpl;
import org.example.newjournal.Utils.JwtUtil;
import org.example.newjournal.entity.User;
import org.example.newjournal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {


    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "OK";
    }


    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) {

        try{
            String username = user.getUserName();
            if(userRepository.findByUserName(username)!= null){
                return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
            }
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("ROLE_USER")); ;
            userRepository.save(user);
            return new ResponseEntity<>("user has been registered successfully!",HttpStatus.CREATED);
        }catch(Exception e){
           
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


   @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
            }
            UserDetails UserDetails = userDetailsService.loadUserByUsername(existingUser.getUserName());
            String token = jwtUtil.generateToken(UserDetails);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}