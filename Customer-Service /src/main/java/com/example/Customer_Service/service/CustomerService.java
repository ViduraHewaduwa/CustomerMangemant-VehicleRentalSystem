package com.example.Customer_Service.service;

import com.example.Customer_Service.dto.CustomerResponse;
import com.example.Customer_Service.dto.LoginRequest;
import com.example.Customer_Service.dto.LoginResponse;
import com.example.Customer_Service.dto.RegisterRequest;
import com.example.Customer_Service.dto.ServiceHistoryResponse;
import com.example.Customer_Service.dto.UpdateCustomerRequest;
import com.example.Customer_Service.entity.Customer;
import com.example.Customer_Service.entity.Role;
import com.example.Customer_Service.entity.ServiceHistory;
import com.example.Customer_Service.exception.ConflictException;
import com.example.Customer_Service.exception.ResourceNotFoundException;
import com.example.Customer_Service.exception.UnauthorizedAccessException;
import com.example.Customer_Service.repository.CustomerRepository;
import com.example.Customer_Service.repository.ServiceHistoryRepository;
import com.example.Customer_Service.security.CustomerUserDetails;
import com.example.Customer_Service.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ServiceHistoryRepository serviceHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public CustomerService(CustomerRepository customerRepository,
                           ServiceHistoryRepository serviceHistoryRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.customerRepository = customerRepository;
        this.serviceHistoryRepository = serviceHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public CustomerResponse register(RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already registered");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setContact(request.getContact());
        customer.setAddress(request.getAddress());
        customer.setRole(Role.ROLE_CUSTOMER);

        return toCustomerResponse(customerRepository.save(customer));
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword())
        );
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return new LoginResponse(token);
    }

    public CustomerResponse getProfile(Long customerId) {
        Customer customer = getCustomerById(customerId);
        validateAccess(customer.getId());
        return toCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer customer = getCustomerById(customerId);
        validateAccess(customer.getId());

        customer.setName(request.getName());
        customer.setContact(request.getContact());
        customer.setAddress(request.getAddress());

        return toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = getCustomerById(customerId);
        validateAccess(customer.getId());
        customerRepository.delete(customer);
    }

    public List<ServiceHistoryResponse> getServiceHistory(Long customerId) {
        getCustomerById(customerId);
        validateAccess(customerId);

        return serviceHistoryRepository.findByCustomerIdOrderByServiceDateDesc(customerId)
                .stream()
                .map(this::toServiceHistoryResponse)
                .toList();
    }

    private Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private void validateAccess(Long requestedCustomerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomerUserDetails principal)) {
            throw new UnauthorizedAccessException("Unauthorized access");
        }

        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Role.ROLE_ADMIN.name()));

        if (!isAdmin && !requestedCustomerId.equals(principal.getCustomerId())) {
            throw new UnauthorizedAccessException("You can only access your own account");
        }
    }

    private CustomerResponse toCustomerResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setContact(customer.getContact());
        response.setAddress(customer.getAddress());
        response.setRole(customer.getRole());
        return response;
    }

    private ServiceHistoryResponse toServiceHistoryResponse(ServiceHistory history) {
        ServiceHistoryResponse response = new ServiceHistoryResponse();
        response.setId(history.getId());
        response.setServiceDate(history.getServiceDate());
        response.setVehicle(history.getVehicle());
        response.setDescription(history.getDescription());
        response.setStatus(history.getStatus());
        response.setCost(history.getCost());
        return response;
    }
}
