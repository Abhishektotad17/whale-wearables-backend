package com.whalewearables.backend.dto;

public class GoogleLoginRequest {

    private String token;

    private String code;

    public GoogleLoginRequest() {

    }

    public GoogleLoginRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "GoogleLoginRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
