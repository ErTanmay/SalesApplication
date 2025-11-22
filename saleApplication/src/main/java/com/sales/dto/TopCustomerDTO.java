package com.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private String customerName;
    private Object value; // Can be totalPayment or shoppingCount
}