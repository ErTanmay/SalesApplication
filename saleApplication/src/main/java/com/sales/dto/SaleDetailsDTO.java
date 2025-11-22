package com.sales.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetailsDTO {
    private Long id;
    
    @NotNull(message = "Shop date is required")
    private LocalDate shopDate;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobileNo;
    
    @NotBlank(message = "Customer name is required")
    private String custName;
    
    @NotNull(message = "Item ID is required")
    private Long itemId;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    private Boolean minor;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private Double price;
    
    private Double payAmount;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String emailId;
    
    private Long version;
}

