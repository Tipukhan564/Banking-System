package com.banking.controller;

import com.banking.model.dto.AuthRequest;
import com.banking.model.dto.AuthResponse;
import com.banking.model.dto.RegisterRequest;
import com.banking.model.entity.Customer;
import com.banking.repository.CustomerRepository;
import com.banking.security.JwtUtil;
import com.banking.service.EncryptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            final String token = jwtUtil.generateToken(userDetails);

            Customer customer = customerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .customerId(customer.getCustomerId())
                    .email(customer.getEmail())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (customerRepository.existsByCnic(request.getCnic())) {
            throw new RuntimeException("CNIC already registered");
        }

        Customer customer = Customer.builder()
                .customerId(generateCustomerId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .cnic(encryptionService.encrypt(request.getCnic()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(Customer.CustomerStatus.ACTIVE)
                .type(Customer.CustomerType.INDIVIDUAL)
                .nadraVerified(false)
                .build();

        customer = customerRepository.save(customer);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(customer.getEmail());
        final String token = jwtUtil.generateToken(userDetails);

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .customerId(customer.getCustomerId())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();

        return ResponseEntity.ok(response);
    }

    private String generateCustomerId() {
        return "CUST" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
