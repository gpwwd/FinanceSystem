package com.financialsystem.dto.response;

import com.financialsystem.domain.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponseDto {
    Long id;
    String username;
    String token;
    String role;
}
