package com.sales.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.sales.dto.PaymentReportDTO;
import com.sales.dto.SaleDetailsDTO;
import com.sales.dto.TopCustomerDTO;
import com.sales.entity.ItemMaster;
import com.sales.entity.SaleDetails;
import com.sales.repository.ItemMasterRepository;
import com.sales.repository.SaleDetailsRepository;

@Service
public class SaleDetailsService {
    
    @Autowired
    private SaleDetailsRepository saleDetailsRepository;
    
    @Autowired
    private ItemMasterRepository itemMasterRepository;
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SaleDetailsDTO createSale(SaleDetailsDTO dto) {
        // Validate item exists and available
        ItemMaster item = itemMasterRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (!item.getAvailableForSale()) {
            throw new RuntimeException("Item is not available for sale");
        }
        
        // Check if item quantity is sufficient
        if (item.getItemQty() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient item quantity");
        }
        
        // Calculate age and check if minor
        int age = Period.between(dto.getDateOfBirth(), LocalDate.now()).getYears();
        boolean isMinor = age < 18;
        dto.setMinor(isMinor);
        
        // Calculate pay amount
        double payAmount = dto.getPrice() * dto.getQuantity();
        
        // Minor validation
        if (isMinor && payAmount > 1000) {
            throw new RuntimeException("Minors can only shop for up to Rs. 1000");
        }
        
        // Maharashtra discount
        if ("Maharashtra".equalsIgnoreCase(dto.getState())) {
            payAmount = payAmount * 0.8; // 20% discount
        }
        
        dto.setPayAmount(payAmount);
        
        SaleDetails sale = convertToEntity(dto, item);
        
        // Update item quantity
        item.setItemQty(item.getItemQty() - dto.getQuantity());
        itemMasterRepository.save(item);
        
        SaleDetails saved = saleDetailsRepository.save(sale);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void cancelSale(Long id) {
        SaleDetails sale = saleDetailsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sale not found"));
        
        // Restore item quantity
        ItemMaster item = sale.getItem();
        item.setItemQty(item.getItemQty() + sale.getQuantity());
        itemMasterRepository.save(item);
        
        saleDetailsRepository.delete(sale);
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SaleDetailsDTO updateSale(Long id, SaleDetailsDTO dto) {
        SaleDetails existing = saleDetailsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sale not found"));
        
        // Check version for optimistic locking
        if (!existing.getVersion().equals(dto.getVersion())) {
            throw new RuntimeException("Sale has been modified by another user");
        }
        
        ItemMaster oldItem = existing.getItem();
        ItemMaster newItem = itemMasterRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (!newItem.getAvailableForSale()) {
            throw new RuntimeException("Item is not available for sale");
        }
        
        // Restore old item quantity
        oldItem.setItemQty(oldItem.getItemQty() + existing.getQuantity());
        itemMasterRepository.save(oldItem);
        
        // Check new item quantity
        if (newItem.getItemQty() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient item quantity");
        }
        
        // Calculate age and minor status
        int age = Period.between(dto.getDateOfBirth(), LocalDate.now()).getYears();
        boolean isMinor = age < 18;
        
        double payAmount = dto.getPrice() * dto.getQuantity();
        
        if (isMinor && payAmount > 1000) {
            throw new RuntimeException("Minors can only shop for up to Rs. 1000");
        }
        
        if ("Maharashtra".equalsIgnoreCase(dto.getState())) {
            payAmount = payAmount * 0.8;
        }
        
        // Update sale details
        existing.setShopDate(dto.getShopDate());
        existing.setMobileNo(dto.getMobileNo());
        existing.setCustName(dto.getCustName());
        existing.setItem(newItem);
        existing.setAddress(dto.getAddress());
        existing.setState(dto.getState());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setMinor(isMinor);
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());
        existing.setPayAmount(payAmount);
        existing.setEmailId(dto.getEmailId());
        
        // Update new item quantity
        newItem.setItemQty(newItem.getItemQty() - dto.getQuantity());
        itemMasterRepository.save(newItem);
        
        SaleDetails updated = saleDetailsRepository.save(existing);
        return convertToDTO(updated);
    }
    
    @Transactional(readOnly = true)
    public Page<SaleDetailsDTO> searchByItemName(String itemName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return saleDetailsRepository.findByItem_ItemNameContainingIgnoreCase(itemName, pageable)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<SaleDetailsDTO> searchByCustomerName(String custName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return saleDetailsRepository.findByCustNameContainingIgnoreCase(custName, pageable)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<SaleDetailsDTO> searchByMobileNo(String mobileNo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return saleDetailsRepository.findByMobileNo(mobileNo, pageable)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<SaleDetailsDTO> searchByAmount(Double amount, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return saleDetailsRepository.findByPayAmountGreaterThanEqual(amount, pageable)
            .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentReportDTO> getPaymentReport() {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDate monthEndDate = currentMonth.atEndOfMonth();
        LocalDate currentMonthStart = currentMonth.atDay(1);
        
        List<Object[]> results = saleDetailsRepository.getItemCustomerWisePaymentReport(
            monthEndDate, currentMonthStart);
        
        return results.stream()
            .map(row -> new PaymentReportDTO(
                (String) row[0],
                (String) row[1],
                (LocalDate) row[2],
                ((Number) row[3]).doubleValue(),
                ((Number) row[4]).doubleValue(),
                ((Number) row[5]).doubleValue()
            ))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TopCustomerDTO> getTop5CustomersByPayment() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Object[]> results = saleDetailsRepository.getTopCustomersByPayment(pageable);
        
        return results.stream()
            .map(row -> new TopCustomerDTO(
                (String) row[0],
                ((Number) row[1]).doubleValue()
            ))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TopCustomerDTO> getTop10CustomersByFrequency() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Object[]> results = saleDetailsRepository.getTopCustomersByFrequency(pageable);
        
        return results.stream()
            .map(row -> new TopCustomerDTO(
                (String) row[0],
                ((Number) row[1]).longValue()
            ))
            .collect(Collectors.toList());
    }
    
    private SaleDetails convertToEntity(SaleDetailsDTO dto, ItemMaster item) {
        SaleDetails sale = new SaleDetails();
        sale.setShopDate(dto.getShopDate());
        sale.setMobileNo(dto.getMobileNo());
        sale.setCustName(dto.getCustName());
        sale.setItem(item);
        sale.setAddress(dto.getAddress());
        sale.setState(dto.getState());
        sale.setDateOfBirth(dto.getDateOfBirth());
        sale.setMinor(dto.getMinor());
        sale.setQuantity(dto.getQuantity());
        sale.setPrice(dto.getPrice());
        sale.setPayAmount(dto.getPayAmount());
        sale.setEmailId(dto.getEmailId());
        return sale;
    }
    
    private SaleDetailsDTO convertToDTO(SaleDetails sale) {
        SaleDetailsDTO dto = new SaleDetailsDTO();
        dto.setId(sale.getId());
        dto.setShopDate(sale.getShopDate());
        dto.setMobileNo(sale.getMobileNo());
        dto.setCustName(sale.getCustName());
        dto.setItemId(sale.getItem().getItemId());
        dto.setAddress(sale.getAddress());
        dto.setState(sale.getState());
        dto.setDateOfBirth(sale.getDateOfBirth());
        dto.setMinor(sale.getMinor());
        dto.setQuantity(sale.getQuantity());
        dto.setPrice(sale.getPrice());
        dto.setPayAmount(sale.getPayAmount());
        dto.setEmailId(sale.getEmailId());
        dto.setVersion(sale.getVersion());
        return dto;
    }
}