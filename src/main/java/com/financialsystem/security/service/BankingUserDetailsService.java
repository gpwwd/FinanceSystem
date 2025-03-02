package com.financialsystem.security.service;

import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.User;
import com.financialsystem.security.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BankingUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public BankingUserDetailsService (UserDetailsRepository repository){
        this.userDetailsRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
