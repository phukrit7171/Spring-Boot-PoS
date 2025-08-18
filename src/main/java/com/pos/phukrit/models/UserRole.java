package com.pos.phukrit.models;

public enum UserRole {
    
    ADMIN,
    STAFF,
    CUSTOMER;

    // You can add methods or properties if needed, for example:
    public String getRoleName() {
        return this.name();
    }
}
