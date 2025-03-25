package com.financialsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ClientResponseDto {
    protected Long id;
    protected String fullName;
    protected String passport;
    protected String identityNumber;
    protected String phone;
    protected String email;
    protected String role;
    protected LocalDateTime createdAt;
    protected boolean isForeign;
    private String status;
}
