package com.marketplace.enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    BUYER,
    SELLER,
    BOTH;
    @JsonCreator
    public static UserRole fromString(String role) {
        if (role == null) {
            return null;
        }

        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    
    }
}
