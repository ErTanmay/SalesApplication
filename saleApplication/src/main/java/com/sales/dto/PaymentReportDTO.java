package com.sales.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReportDTO {
    private String itemName;
    private String customerName;
    private LocalDate monthEndDate;
    private Double lastMonthTotal;
    private Double currentMonthTotal;
    private Double totalPayment;
}

