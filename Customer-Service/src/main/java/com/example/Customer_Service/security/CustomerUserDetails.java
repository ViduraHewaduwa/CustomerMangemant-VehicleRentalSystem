package com.example.Customer_Service.security;

import com.example.Customer_Service.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomerUserDetails implements UserDetails {

    private final Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }

    public Long getCustomerId() {
        return customer.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(customer.getRole().name()));
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }
}
