package com.financialsystem.domain.model.user;

public enum Role {
    CLIENT, ADMIN, OPERATOR, MANAGER, SPECIALIST;

    public static Role fromString(String roleName) {
        return Role.valueOf(roleName.toUpperCase());
    }
}
