package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.InventoryItem;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class InventoryServiceImpl {

    private final InventoryItemRepository invRepo;

    public InventoryResponse create(InventoryRequest req) {
        if (req.getSku() != null && invRepo.findBySku(req.getSku()).isPresent())
            throw new DuplicateResourceException("SKU already exists: " + req.getSku());

        InventoryItem item = InventoryItem.builder()
            .name(req.getName()).category(req.getCategory()).brand(req.getBrand())
            .sku(req.getSku()).stock(req.getStock()).minStock(req.getMinStock())
            .unit(req.getUnit()).costPrice(req.getCostPrice()).sellingPrice(req.getSellingPrice())
            .supplier(req.getSupplier()).expiryDate(req.getExpiryDate())
            .lastRestocked(LocalDate.now())
            .status(computeStatus(req.getStock(), req.getMinStock()))
            .build();
        return toResponse(invRepo.save(item));
    }

    @Transactional(readOnly = true)
    public PageResponse<InventoryResponse> list(String category, String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<InventoryItem> p;
        if (category != null && category != "") {
            List<InventoryItem> filtered = invRepo.findByCategory(category);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + size, filtered.size());
            p = new PageImpl<>(start >= filtered.size() ? List.of() : filtered.subList(start, end), pageable, filtered.size());
        } else if (status != null) {
            List<InventoryItem> filtered = invRepo.findByStatus(status);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + size, filtered.size());
            p = new PageImpl<>(start >= filtered.size() ? List.of() : filtered.subList(start, end), pageable, filtered.size());
        } else {
            p = invRepo.findAll(pageable);
        }
        return PageResponse.<InventoryResponse>builder()
            .content(p.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(p.getNumber()).size(p.getSize())
            .totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStock() {
        return invRepo.findLowStock().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public InventoryResponse update(Long id, InventoryRequest req) {
        InventoryItem item = findOrThrow(id);
        item.setName(req.getName()); item.setCategory(req.getCategory());
        item.setBrand(req.getBrand()); item.setSku(req.getSku());
        item.setStock(req.getStock()); item.setMinStock(req.getMinStock());
        item.setUnit(req.getUnit()); item.setCostPrice(req.getCostPrice());
        item.setSellingPrice(req.getSellingPrice()); item.setSupplier(req.getSupplier());
        item.setExpiryDate(req.getExpiryDate());
        item.setStatus(computeStatus(req.getStock(), req.getMinStock()));
        return toResponse(invRepo.save(item));
    }

    public InventoryResponse restock(Long id, int quantity) {
        InventoryItem item = findOrThrow(id);
        item.setStock(item.getStock() + quantity);
        item.setLastRestocked(LocalDate.now());
        item.setStatus(computeStatus(item.getStock(), item.getMinStock()));
        return toResponse(invRepo.save(item));
    }

    public void delete(Long id) { invRepo.delete(findOrThrow(id)); }

    private InventoryItem findOrThrow(Long id) {
        return invRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));
    }

    private String computeStatus(int stock, int minStock) {
        if (stock <= 0) return "out-of-stock";
        if (stock <= minStock) return "low-stock";
        return "in-stock";
    }

    InventoryResponse toResponse(InventoryItem i) {
        return InventoryResponse.builder()
            .id(i.getId()).name(i.getName()).category(i.getCategory()).brand(i.getBrand())
            .sku(i.getSku()).stock(i.getStock()).minStock(i.getMinStock()).unit(i.getUnit())
            .costPrice(i.getCostPrice()).sellingPrice(i.getSellingPrice()).supplier(i.getSupplier())
            .lastRestocked(i.getLastRestocked()).expiryDate(i.getExpiryDate()).status(i.getStatus())
            .build();
    }
}
