package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.ClientDatabaseDto;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                this.role, this.createdAt, this.isForeign, this.banksIds
        );
    }

    @Override
    protected void assignRole() {
        this.role = Role.CLIENT;
    }

    public static Client create (PendingClientDatabaseDto dto){
        if(!dto.getStatus().equals(PendingClientStatus.APPROVED)){
            throw new RuntimeException("Client status is not APPROVED");
        }

        Client client = new Client(dto);
        client.assignRole();
        client.isForeign = dto.isForeign();
        return client;
    }

    public void checkConnectionWithBank(Long bankId){
        if (!this.banksIds.contains(bankId)) {
            throw new IllegalArgumentException("Клиент не связан с данным банком");
        }
    }
}
