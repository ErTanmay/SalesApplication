package com.sales.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sales.dto.PaymentReportDTO;
import com.sales.dto.SaleDetailsDTO;
import com.sales.dto.TopCustomerDTO;
import com.sales.service.SaleDetailsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleDetailsController {
    
    @Autowired
    private SaleDetailsService saleDetailsService;
    
    @PostMapping
    public ResponseEntity<?> createSale(@Valid @RequestBody SaleDetailsDTO dto) {
        try {
            SaleDetailsDTO created = saleDetailsService.createSale(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelSale(@PathVariable Long id) {
        try {
            saleDetailsService.cancelSale(id);
            return ResponseEntity.ok("Sale cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable Long id, @Valid @RequestBody SaleDetailsDTO dto) {
        try {
            SaleDetailsDTO updated = saleDetailsService.updateSale(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/search/item")
    public ResponseEntity<Page<SaleDetailsDTO>> searchByItemName(
            @RequestParam String itemName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SaleDetailsDTO> results = saleDetailsService.searchByItemName(itemName, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/search/customer")
    public ResponseEntity<Page<SaleDetailsDTO>> searchByCustomerName(
            @RequestParam String custName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SaleDetailsDTO> results = saleDetailsService.searchByCustomerName(custName, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/search/mobile")
    public ResponseEntity<Page<SaleDetailsDTO>> searchByMobileNo(
            @RequestParam String mobileNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SaleDetailsDTO> results = saleDetailsService.searchByMobileNo(mobileNo, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/search/amount")
    public ResponseEntity<Page<SaleDetailsDTO>> searchByAmount(
            @RequestParam Double amount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SaleDetailsDTO> results = saleDetailsService.searchByAmount(amount, page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/reports/payment")
    public ResponseEntity<List<PaymentReportDTO>> getPaymentReport() {
        List<PaymentReportDTO> report = saleDetailsService.getPaymentReport();
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/reports/top-customers/payment")
    public ResponseEntity<List<TopCustomerDTO>> getTop5CustomersByPayment() {
        List<TopCustomerDTO> customers = saleDetailsService.getTop5CustomersByPayment();
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/reports/top-customers/frequency")
    public ResponseEntity<List<TopCustomerDTO>> getTop10CustomersByFrequency() {
        List<TopCustomerDTO> customers = saleDetailsService.getTop10CustomersByFrequency();
        return ResponseEntity.ok(customers);
    }
}