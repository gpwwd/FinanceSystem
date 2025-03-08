package com.financialsystem.service;

import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.dto.response.UserAuthResponseDto;
import com.financialsystem.exception.custom.BadRequestException;
import com.financialsystem.repository.user.PendingClientRepository;
import com.financialsystem.security.repository.UserDetailsRepository;
import com.financialsystem.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final PendingClientRepository pendingClientRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(PendingClientRepository pendingClientRepository, UserDetailsRepository userDetailsRepository,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.pendingClientRepository = pendingClientRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public Long registerClient(ClientRegistrationRequest request) {
        checkIfUserInTheSystem(request.fullName());
        PendingClient pendingClient = PendingClient.create(request);
        return pendingClientRepository.create(pendingClient);
    }

    private void checkIfUserInTheSystem(String username) {
        Optional<BankingUserDetails> userOptional = userDetailsRepository.findByName(username);

        if (userOptional.isPresent()) {
            throw new BadRequestException("User " + username + " is already in use");
        }

        Optional<PendingClient> pendingClientOptional = pendingClientRepository.findByName(username);

        if (pendingClientOptional.isPresent()) {
            throw new BadRequestException("User with name '" + username + "' is already pending for verification.");
        }
    }

    public UserAuthResponseDto login(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        BankingUserDetails userDetails = (BankingUserDetails) authentication.getPrincipal();

        var token = jwtService.generateToken(userDetails);

        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .toList();

        return new UserAuthResponseDto(
                userDetails.getId(), userDetails.getUsername(), token, authorities.getFirst()
        );
    }
}
