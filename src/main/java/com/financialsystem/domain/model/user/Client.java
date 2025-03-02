package com.financialsystem.domain.model.user;

import com.financialsystem.dto.database.user.ClientDatabaseDto;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;

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
}
