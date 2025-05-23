package com.hemreozalp.book_management_api.service;

import com.hemreozalp.book_management_api.model.User;
import com.hemreozalp.book_management_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String password){
        if (userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");

        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password){
        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isPresent()){
            User user = optUser.get();
            if (passwordEncoder.matches(password, user.getPassword())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
