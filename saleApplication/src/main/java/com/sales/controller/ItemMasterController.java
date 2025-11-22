package com.sales.controller;

import com.sales.dto.ItemMasterDTO;
import com.sales.service.ItemMasterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemMasterController {
    
    @Autowired
    private ItemMasterService itemMasterService;
    
    @PostMapping
    public ResponseEntity<ItemMasterDTO> createItem(@Valid @RequestBody ItemMasterDTO dto) {
        try {
            ItemMasterDTO created = itemMasterService.createItem(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<ItemMasterDTO>> getAllItems() {
        List<ItemMasterDTO> items = itemMasterService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemMasterDTO> getItemById(@PathVariable Long id) {
        try {
            ItemMasterDTO item = itemMasterService.getItemById(id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}