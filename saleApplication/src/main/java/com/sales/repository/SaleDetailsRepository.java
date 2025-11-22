package com.sales.repository;

import com.sales.entity.SaleDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {
    
    Page<SaleDetails> findByItem_ItemNameContainingIgnoreCase(String itemName, Pageable pageable);
    
    Page<SaleDetails> findByCustNameContainingIgnoreCase(String custName, Pageable pageable);
    
    Page<SaleDetails> findByMobileNo(String mobileNo, Pageable pageable);
    
    Page<SaleDetails> findByPayAmountGreaterThanEqual(Double amount, Pageable pageable);
    
    @Query("SELECT s.item.itemName as itemName, s.custName as customerName, " +
           ":monthEndDate as monthEndDate, " +
           "COALESCE(SUM(CASE WHEN s.shopDate < :currentMonthStart THEN s.payAmount ELSE 0 END), 0) as lastMonthTotal, " +
           "COALESCE(SUM(CASE WHEN s.shopDate >= :currentMonthStart AND s.shopDate <= :monthEndDate THEN s.payAmount ELSE 0 END), 0) as currentMonthTotal, " +
           "COALESCE(SUM(s.payAmount), 0) as totalPayment " +
           "FROM SaleDetails s " +
           "WHERE s.shopDate <= :monthEndDate " +
           "GROUP BY s.item.itemName, s.custName")
    List<Object[]> getItemCustomerWisePaymentReport(
        @Param("monthEndDate") LocalDate monthEndDate,
        @Param("currentMonthStart") LocalDate currentMonthStart
    );
    
    @Query("SELECT s.custName, SUM(s.payAmount) as totalPayment " +
           "FROM SaleDetails s " +
           "GROUP BY s.custName " +
           "ORDER BY totalPayment DESC")
    List<Object[]> getTopCustomersByPayment(Pageable pageable);
    
    @Query("SELECT s.custName, COUNT(s) as shoppingCount " +
           "FROM SaleDetails s " +
           "GROUP BY s.custName " +
           "ORDER BY shoppingCount DESC")
    List<Object[]> getTopCustomersByFrequency(Pageable pageable);
}