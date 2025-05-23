package com.hemreozalp.book_management_api.service;

import com.hemreozalp.book_management_api.model.User;
import com.hemreozalp.book_management_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success(){
        String username = "testuser";
        String rawPassword = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User user = authService.register(username, rawPassword);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("ROLE_USER", user.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_usernameTaken_throwsException() {
        String username = "testuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(username, "password");
        });

        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void authenticate_success() {
        String username = "testuser";
        String rawPassword = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);

        Optional<User> result = authService.authenticate(username, rawPassword);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void authenticate_wrongPassword_returnsEmpty() {
        String username = "testuser";
        String rawPassword = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(false);

        Optional<User> result = authService.authenticate(username, rawPassword);

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticate_userNotFound_returnsEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = authService.authenticate("unknown", "password");

        assertTrue(result.isEmpty());
    }
}
