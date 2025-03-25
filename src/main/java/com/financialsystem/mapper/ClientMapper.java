package com.financialsystem.mapper;


import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.response.ClientResponseDto;

public class ClientMapper {

    public static ClientResponseDto toPendingClientResponseDto(PendingClient client) {
        var d = client.toDto();
        return new ClientResponseDto(d.getId(), d.getFullName(), d.getPassport(), d.getIdentityNumber(), d.getPhone(), d.getEmail(),
        d.getRole().name(), d.getCreatedAt(), d.isForeign(), d.getStatus().name());
    }
}