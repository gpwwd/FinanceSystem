package com.financialsystem.service;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //private final PendingUserRepository pendingUserRepository;
    //private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long registerClient(String username, String password) {
//        if (userRepository.findByUsername(username) != null || pendingUserRepository.findByUsername(username) != null) {
//            throw new RuntimeException("User already exists");
//        }
//
//       // String encodedPassword = passwordEncoder.encode(password);
//        pendingUserRepository.savePendingUser(username, password);
        Client client = Client.create(username, "passport", "idedntityNumber", "phone",
                "email", false, LocalDateTime.now());
        Long id = userRepository.create(client);
        return id;
    }
}
