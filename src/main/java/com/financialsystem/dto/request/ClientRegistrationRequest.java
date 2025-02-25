package com.financialsystem.dto.request;

import com.financialsystem.domain.model.user.Role;

import java.time.LocalDateTime;
import java.util.List;

public record ClientRegistrationRequest(
        String fullName,
        String passport,
        String identityNumber,
        String phone,
        String email,
        boolean isForeign
) {}
