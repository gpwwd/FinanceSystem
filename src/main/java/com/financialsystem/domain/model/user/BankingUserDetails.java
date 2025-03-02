package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.UserDatabaseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class BankingUserDetails extends User implements UserDetails {

    protected BankingUserDetails(UserDatabaseDto user) {
        super(user);
    }

    public BankingUserDetails(String fullName, String passport, String identityNumber, String phone, String email,
                              LocalDateTime createdAt, String password) {
        super(fullName, passport, identityNumber, phone, email, createdAt, password);
    }

    public BankingUserDetails(long id, String fullName, String passportSeriesNumber, String identityNumber, String phone,
                              String email, Role role, String password, LocalDateTime createdAt) {
        super(fullName, passportSeriesNumber, identityNumber, phone, email, createdAt, password);
        this.id = id;
        this.role = role;
    }

    @Override
    protected void assignRole() {}

    @Override
    public UserDatabaseDto toDto() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.fullName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public Long getId(){
        return this.id;
    }
}
