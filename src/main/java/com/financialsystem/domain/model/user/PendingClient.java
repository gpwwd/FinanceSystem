package com.financialsystem.domain.model.user;

import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class PendingClient extends Client {
    private PendingEntityStatus status;

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
                this.id, this.fullName, this.passport, this.identityNumber, this.phone,
                this.email, this.role, this.password, this.createdAt,
                this.isForeign, this.status
        );
    }

    @Override
    protected void assignRole() {
        super.assignRole();
    }

    public static PendingClient create (ClientRegistrationRequest request){
        return new PendingClient(new PendingClientDatabaseDto(
                null, request.fullName(), request.passport(), request.identityNumber(),
                request.phone(), request.email(), Role.CLIENT,
                encodePassword(request.password()), LocalDateTime.now(),
                request.isForeign(), PendingEntityStatus.PENDING
        ));
    }

    public void setApprovedStatus() {
        this.status = PendingEntityStatus.APPROVED;
    }

    public void rejectRegistration() {
        this.status = PendingEntityStatus.REJECTED;
    }
}
