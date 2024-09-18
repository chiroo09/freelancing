package com.laundryservice.maxcleaners.dto;

import javax.validation.constraints.NotBlank;

/**
 * Author Tejesh
 */
public class LoginRequest {

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String password;

    // Getters and setters

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
