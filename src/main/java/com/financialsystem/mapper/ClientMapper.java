package com.financialsystem.mapper;


import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.response.PendingClientResponseDto;

public class ClientMapper {

    public static PendingClientResponseDto toPendingClientResponseDto(PendingClient client) {
        var d = client.toDto();
        return new PendingClientResponseDto(d.getId(), d.getFullName(), d.getPassport(), d.getIdentityNumber(), d.getPhone(), d.getEmail(),
        d.getRole().name(), d.getCreatedAt(), d.isForeign(), d.getStatus().name());
    }
}