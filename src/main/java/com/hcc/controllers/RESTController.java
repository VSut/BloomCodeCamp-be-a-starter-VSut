package com.hcc.controllers;

import com.hcc.dto.AssignmentRequest;
import com.hcc.dto.AuthCredentialsRequest;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;

import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.utils.CustomPasswordEncoder;
import com.hcc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController

public class RESTController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;
    /**
     * Registers a new user.
     *
     * @param authCredentialsRequest the authentication credentials request
     * @return a response entity with a message indicating the result of the registration
     */
    @PostMapping("/api/auth/register")
    public ResponseEntity<String> register(@RequestBody AuthCredentialsRequest authCredentialsRequest) {
        if (userRepository.existsByUsername(authCredentialsRequest.getUsername())) {
            return ResponseEntity.status(400).body("Username already exists");
        }
        User user = new User();
        user.setUsername(authCredentialsRequest.getUsername());
        user.setPassword(customPasswordEncoder.getPasswordEncoder().encode(authCredentialsRequest.getPassword()));
        user.setCohortStartDate(new Date());
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param authCredentialsRequest the authentication credentials request
     * @return a response entity with the generated JWT token or an error message
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthCredentialsRequest authCredentialsRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authCredentialsRequest.getUsername(), authCredentialsRequest.getPassword())
            );
            User user = userRepository.findByUsername(authCredentialsRequest.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
    /**
     * Validates a JWT token.
     *
     * @param token the JWT token
     * @return a response entity indicating whether the token is valid or not
     */
    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7); // Remove "Bearer " prefix
            String username = jwtUtil.getUsernameFromToken(jwtToken);
            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null && jwtUtil.validateToken(jwtToken, user)) {
                return ResponseEntity.ok("Token is valid");
            } else {
                return ResponseEntity.status(401).body("Invalid token!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
    /**
     * Retrieves assignments for the logged-in user.
     *
     * @param token the JWT token
     * @return a response entity with the list of assignments for the logged-in user
     */
    @GetMapping("/api/assignments")
    public ResponseEntity<List<Assignment>> getAssignmentsForLoggedInUser(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String username = jwtUtil.getUsernameFromToken(jwtToken);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(null);
        }

        List<Assignment> assignments = assignmentRepository.findByUser(user);
        return ResponseEntity.ok(assignments);
    }
    /**
     * Retrieves an assignment by its ID.
     *
     * @param id    the assignment ID
     * @param token the JWT token
     * @return a response entity with the assignment or an error message
     */
    @GetMapping("/api/assignments/{id}") public ResponseEntity  getAssignmentById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String username = jwtUtil.getUsernameFromToken(jwtToken);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(null);
        }

        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(assignment);
    }
    /**
     * Updates an assignment.
     *
     * @param id                 the assignment ID
     * @param token              the JWT token
     * @param assignmentRequest  the assignment request
     * @return a response entity with a message indicating the result of the update
     */
    @PutMapping("/api/assignments/{id}")
    public ResponseEntity<String> updateAssignment(@PathVariable Long id, @RequestHeader("Authorization") String token, @RequestBody AssignmentRequest assignmentRequest) {
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String username = jwtUtil.getUsernameFromToken(jwtToken);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null || !assignment.getUser().equals(user)) {
            return ResponseEntity.status(404).body("Assignment not found");
        }

        assignment.setGithubUrl(assignmentRequest.getGithubUrl());
        assignment.setReviewVideoUrl(assignmentRequest.getReviewVideoUrl());
        assignment.setStatus(assignmentRequest.getStatus());
        assignment.setBranch(assignmentRequest.getBranch());
        assignment.setNumber(assignmentRequest.getNumber());

        assignmentRepository.save(assignment);

        return ResponseEntity.ok("Assignment updated successfully");
    }
    /**
     * Creates a new assignment.
     *
     * @param assignmentRequest the assignment request
     * @return a response entity with a message indicating the result of the creation
     */
    @PostMapping("/api/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody AssignmentRequest assignmentRequest) {
        if (!userRepository.existsByUsername(assignmentRequest.getUsername())) {
            return ResponseEntity.status(404).body("User not found");
        }
        String username = assignmentRequest.getUsername();
        User user = userRepository.findByUsername(username).orElse(null);
        Assignment assignment = new Assignment();
        assignment.setStatus(assignmentRequest.getStatus());
        assignment.setNumber(assignmentRequest.getNumber());
        assignment.setGithubUrl(assignmentRequest.getGithubUrl());
        assignment.setBranch(assignmentRequest.getBranch());
        assignment.setUser(user);
        assignment.setReviewVideoUrl(assignmentRequest.getReviewVideoUrl());
        assignmentRepository.save(assignment);

        return ResponseEntity.ok("Assignment created successfully");
    }
}
