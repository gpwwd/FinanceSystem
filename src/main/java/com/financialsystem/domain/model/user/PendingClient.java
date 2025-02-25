package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.request.ClientRegistrationRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PendingClient extends Client {
    private PendingClientStatus status;

    protected PendingClient(PendingClientDatabaseDto dto) {
        super(dto);
        this.status = dto.getStatus();
    }

    public static PendingClient fromDto(PendingClientDatabaseDto dto){
        return new PendingClient(dto);
    }

    @Override
    public PendingClientDatabaseDto toDto() {
        return new PendingClientDatabaseDto(
                this.id, this.fullName, this.passport, this.identityNumber, this.phone, this.email, this.role, this.createdAt,
                this.isForeign, this.status
        );
    }

    @Override
    protected void assignRole() {
        super.assignRole();
    }

    public static PendingClient create (ClientRegistrationRequest request){
        return new PendingClient(new PendingClientDatabaseDto(
                null, request.fullName(), request.passport(), request.identityNumber(), request.phone(), request.email(),
                Role.CLIENT, LocalDateTime.now(), request.isForeign(), PendingClientStatus.PENDING
        ));
    }

    public void approveRegistration() {
        this.status = PendingClientStatus.APPROVED;
    }

    public void rejectRegistration() {
        this.status = PendingClientStatus.REJECTED;
    }
}
