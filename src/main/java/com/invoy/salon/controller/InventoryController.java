package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.InventoryServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/inventory") @RequiredArgsConstructor @Tag(name = "Inventory")
public class InventoryController {
    private final InventoryServiceImpl inventoryService;
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> create(@Valid @RequestBody InventoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Item created", inventoryService.create(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<InventoryResponse>>> list(
        @RequestParam(required=false) String category,
        @RequestParam(required=false) String status,
        @RequestParam(required=false) String search,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.list(category, status, search, page, size)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> update(@PathVariable Long id, @Valid @RequestBody InventoryRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated", inventoryService.update(id, req)));
    }
    @PatchMapping("/{id}/restock")
    public ResponseEntity<ApiResponse<InventoryResponse>> restock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(ApiResponse.ok("Stock updated", inventoryService.restock(id, quantity)));
    }
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> lowStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStock()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        inventoryService.delete(id); return ResponseEntity.ok(ApiResponse.ok("Item deleted"));
    }
}
