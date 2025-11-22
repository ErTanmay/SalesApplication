package com.sales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMasterDTO {
    private Long itemId;
    
    @NotBlank(message = "Item name is required")
    private String itemName;
    
    private Integer itemQty;
    
    private Boolean availableForSale = true;
}