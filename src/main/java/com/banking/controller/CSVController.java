package com.banking.controller;

import com.banking.model.entity.Customer;
import com.banking.service.CSVProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CSVController {

    private final CSVProcessingService csvProcessingService;

    @PostMapping("/bulk-account-opening")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> bulkAccountOpening(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!csvProcessingService.validateCSVFormat(file)) {
            throw new RuntimeException("Invalid CSV format");
        }

        List<Customer> customers = csvProcessingService.processBulkAccountOpening(file);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("customersCreated", customers.size());
        response.put("customers", customers);

        return ResponseEntity.ok(response);
    }
}
