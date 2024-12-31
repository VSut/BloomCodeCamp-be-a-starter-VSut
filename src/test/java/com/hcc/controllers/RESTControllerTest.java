package com.hcc.controllers;

import com.hcc.dto.AssignmentRequest;
import com.hcc.dto.AuthCredentialsRequest;
import com.hcc.entities.Assignment;
import com.hcc.entities.User;
import com.hcc.repositories.AssignmentRepository;
import com.hcc.repositories.UserRepository;
import com.hcc.utils.CustomPasswordEncoder;
import com.hcc.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RESTControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomPasswordEncoder customPasswordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private RESTController restController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testLogin() {
        AuthCredentialsRequest request = new AuthCredentialsRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = new User();
        user.setUsername("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("token");

        ResponseEntity<?> response = restController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody());
    }

    @Test
    void testValidateToken() {
        String token = "Bearer token";
        String jwtToken = "token";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        when(jwtUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtUtil.validateToken(jwtToken, user)).thenReturn(true);

        ResponseEntity<?> response = restController.validateToken(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Token is valid", response.getBody());
    }

    @Test
    void testGetAssignmentsForLoggedInUser() {
        String token = "Bearer token";
        String jwtToken = "token";
        String username = "testuser";

        User user = new User();
        user.setUsername(username);

        when(jwtUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(assignmentRepository.findByUser(user)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = restController.getAssignmentsForLoggedInUser(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testGetAssignmentById() {
        String token = "Bearer token";
        String jwtToken = "token";
        String username = "testuser";
        Long id = 1L;

        User user = new User();
        user.setUsername(username);

        Assignment assignment = new Assignment();
        assignment.setId(id);

        when(jwtUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(assignment));

        ResponseEntity<?> response = restController.getAssignmentById(id, token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(assignment, response.getBody());
    }

    @Test
    void testUpdateAssignment() {
        String token = "Bearer token";
        String jwtToken = "token";
        String username = "testuser";
        Long id = 1L;

        User user = new User();
        user.setUsername(username);

        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setUser(user);

        AssignmentRequest request = new AssignmentRequest();
        request.setGithubUrl("githubUrl");
        request.setReviewVideoUrl("reviewVideoUrl");
        request.setStatus("status");
        request.setBranch("branch");
        request.setNumber(1);

        when(jwtUtil.getUsernameFromToken(jwtToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(assignment));

        ResponseEntity<String> response = restController.updateAssignment(id, token, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Assignment updated successfully", response.getBody());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void testCreateAssignment() {
        AssignmentRequest request = new AssignmentRequest();
        request.setUsername("testuser");
        request.setGithubUrl("githubUrl");
        request.setReviewVideoUrl("reviewVideoUrl");
        request.setStatus("status");
        request.setBranch("branch");
        request.setNumber(1);

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = restController.createAssignment(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Assignment created successfully", response.getBody());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }
}