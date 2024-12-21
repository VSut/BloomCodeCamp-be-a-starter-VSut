package com.hcc.dto;

public class AuthCredentialsRequest {

    private String username;
    private String password;

    public AuthCredentialsRequest() {
    }
    public AuthCredentialsRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}