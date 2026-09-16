package com.Library.Management.service;


import com.Library.Management.dto.RegisterRequest;
import com.Library.Management.entity.UserRole;
import com.Library.Management.entity.User;
import com.Library.Management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
    }
}