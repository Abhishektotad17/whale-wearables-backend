package com.whalewearables.backend.dto;

import java.util.Objects;

public class AuthRequest {

    private String identifier;
    private String password;

    public AuthRequest() {
    }
    public AuthRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "identifier='" + identifier + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthRequest that)) return false;
        return Objects.equals(getIdentifier(), that.getIdentifier()) && Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier(), getPassword());
    }
}
