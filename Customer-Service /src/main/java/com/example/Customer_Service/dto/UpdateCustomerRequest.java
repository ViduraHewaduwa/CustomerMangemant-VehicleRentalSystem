package com.example.Customer_Service.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateCustomerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String contact;

    @NotBlank
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
