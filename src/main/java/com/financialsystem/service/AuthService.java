package com.financialsystem.service;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.repository.user.PendingClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final PendingClientRepository pendingClientRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(PendingClientRepository pendingClientRepository) {
        this.pendingClientRepository = pendingClientRepository;
    }

    @Transactional
    public Long registerClient(ClientRegistrationRequest request) {
        PendingClient pendingClient = PendingClient.create(request);
        return pendingClientRepository.create(pendingClient);
    }
}
