package com.hcc.services;

import com.hcc.utils.CustomPasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailServiceImplTest {

    @Mock
    private CustomPasswordEncoder customPasswordEncoder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailServiceImpl userDetailServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(customPasswordEncoder.getPasswordEncoder()).thenReturn(passwordEncoder);
    }

    @Test
    public void testLoadUserByUsername_ValidUsername() {
        String username = "testUser";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_EmptyUsername() {
        String username = "";

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailServiceImpl.loadUserByUsername(username);
        });

        assertEquals("Username is empty", exception.getMessage());
    }



}