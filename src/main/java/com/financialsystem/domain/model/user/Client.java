package com.financialsystem.domain.model.user;

import com.financialsystem.dto.user.ClientDatabaseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private final List<Long> accountIds = new ArrayList<>();
    protected boolean isForeign;

    protected Client(ClientDatabaseDto dto){
        super(dto);
        isForeign = dto.isForeign();
    }

    public static Client fromDto(ClientDatabaseDto dto){
        return new Client(dto);
    }

    @Override
    public ClientDatabaseDto toDto() {
        return new ClientDatabaseDto(
                this.id,
                this.fullName,
                this.passport,
                this.identityNumber,
                this.phone,
                this.email,
                this.role,
                this.createdAt,
                this.isForeign,
                this.enterpriseId
        );
    }

    private Client(String fullName, String passport, String identityNumber,
                  String phone, String email, LocalDateTime createdAt) {
        super(fullName, passport, identityNumber, phone, email, createdAt);
    }

    @Override
    protected void assignRole() {
        this.role = Role.CLIENT;
    }

    public static Client create (String fullName, String passport, String email, String phone,
                                 String passwordHash, boolean isForeign, LocalDateTime createdAt){
        Client client = new Client(fullName, passport, email, phone, passwordHash, createdAt);
        client.isForeign = isForeign;
        client.assignRole();
        return client;
    }
}
