package com.sales.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sales.dto.ItemMasterDTO;
import com.sales.entity.ItemMaster;
import com.sales.repository.ItemMasterRepository;

@Service
public class ItemMasterService {
    
    @Autowired
    private ItemMasterRepository itemMasterRepository;
    
    @Transactional
    public ItemMasterDTO createItem(ItemMasterDTO dto) {
        ItemMaster item = new ItemMaster();
        item.setItemName(dto.getItemName());
        item.setItemQty(dto.getItemQty());
        item.setAvailableForSale(dto.getAvailableForSale());
        
        ItemMaster saved = itemMasterRepository.save(item);
        return convertToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<ItemMasterDTO> getAllItems() {
        return itemMasterRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ItemMasterDTO getItemById(Long id) {
        ItemMaster item = itemMasterRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return convertToDTO(item);
    }
    
    private ItemMasterDTO convertToDTO(ItemMaster item) {
        ItemMasterDTO dto = new ItemMasterDTO();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setItemQty(item.getItemQty());
        dto.setAvailableForSale(item.getAvailableForSale());
        return dto;
    }
}