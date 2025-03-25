package com.financialsystem.domain.model.user;

import com.financialsystem.domain.status.PendingEntityStatus;
import com.financialsystem.dto.database.user.ClientDatabaseDto;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.response.ClientResponseDto;

public class Client extends User {
    protected boolean isForeign;

    protected Client(ClientDatabaseDto dto){
        super(dto);
        this.isForeign = dto.isForeign();
    }

    public static Client fromDto(ClientDatabaseDto dto){
        return new Client(dto);
    }

    @Override
    public ClientDatabaseDto toDto() {
        return new ClientDatabaseDto(
                this.id, this.fullName, this.passport, this.identityNumber, this.phone, this.email,
                this.role, this.password, this.createdAt, this.isForeign
        );
    }

    public ClientResponseDto toResponseDto() {
        return new ClientResponseDto(
                this.id, this.fullName, this.passport, this.identityNumber, this.phone, this.email,
                this.role.toString(), this.createdAt, this.isForeign, PendingEntityStatus.APPROVED.toString()
        );
    }

    @Override
    protected void assignRole() {
        this.role = Role.CLIENT;
    }

    public static Client create (PendingClientDatabaseDto dto){
        if(!dto.getStatus().equals(PendingEntityStatus.APPROVED)){
            throw new IllegalStateException("Client status is not APPROVED");
        }

        Client client = new Client(dto);
        client.assignRole();
        client.isForeign = dto.isForeign();
        return client;
    }
}
