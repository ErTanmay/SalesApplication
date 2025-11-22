package com.sales.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itemmaster")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    
    @Column(nullable = false)
    private String itemName;
    
    @Column(nullable = false)
    private Integer itemQty;
    
    @Column(nullable = false)
    private Boolean availableForSale = true;
}