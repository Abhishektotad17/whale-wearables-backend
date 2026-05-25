package com.whalewearables.backend.dto;

import com.whalewearables.backend.model.Role;

import java.util.Set;

public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String provider;
    private String picture;
    private Set<Role> roles;

    public UserDto() {
    }

    public UserDto(Long id, String name, String email, String provider, String picture, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.picture = picture;
        this.roles = roles;
    }

    // Factory method
    public static UserDto fromEntity(com.whalewearables.backend.model.User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProvider(),
                user.getPicture(),
                user.getRoles()
        );
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public Set<Role> getRoles() { return roles; }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", picture='" + picture + '\'' +
                ", roles=" + roles +
                '}';
    }
}
