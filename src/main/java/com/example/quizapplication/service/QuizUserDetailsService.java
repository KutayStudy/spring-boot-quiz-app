package com.example.quizapplication.service;

import com.example.quizapplication.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizUserDetailsService implements UserDetailsService {

    private final Map<String, User> userHashMap = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public QuizUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String email, String password, String role){
        String encoded = passwordEncoder.encode(password);
        userHashMap.put(username, new User(username,email,encoded,role));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userHashMap.get(username);

        if(user == null)
            throw new UsernameNotFoundException(username);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
