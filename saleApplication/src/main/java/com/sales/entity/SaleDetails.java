package com.sales.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "saledetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate shopDate;
    
    @Column(nullable = false, length = 10)
    private String mobileNo;
    
    @Column(nullable = false)
    private String custName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemMaster item;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(nullable = false)
    private Boolean minor;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Double payAmount;
    
    @Column(nullable = false)
    private String emailId;
    
    @Version
    private Long version; // For optimistic locking
}