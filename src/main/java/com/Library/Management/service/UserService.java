package com.Library.Management.service;


import com.Library.Management.dto.RegisterRequest;
import com.Library.Management.entity.UserRole;
import com.Library.Management.entity.User;
import com.Library.Management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User already registered by email{}",request.getEmail());
            throw new RuntimeException("Email already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setUserRole(UserRole.ADMIN);
        user.setUsername(request.getUsername());
        userRepository.save(user);
        log.info("User with details {}  created",user);
    }
}