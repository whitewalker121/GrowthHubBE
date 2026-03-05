package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.ServiceCategory;
import com.growthhub.salon.service.impl.ServiceCatalogServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/services") @RequiredArgsConstructor @Tag(name = "Services")
public class ServiceController {
    private final ServiceCatalogServiceImpl svc;
    @PostMapping
    public ResponseEntity<ApiResponse<ServiceResponse>> create(@Valid @RequestBody ServiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Service created", svc.createService(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> list(@RequestParam(required=false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(svc.listServices(status)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(svc.getService(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponse>> update(@PathVariable Long id, @Valid @RequestBody ServiceRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Service updated", svc.updateService(id, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        svc.deleteService(id); return ResponseEntity.ok(ApiResponse.ok("Service deactivated"));
    }
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<ServiceCategory>>> categories() {
        return ResponseEntity.ok(ApiResponse.ok(svc.listCategories()));
    }
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<ServiceCategory>> createCategory(
        @RequestParam String name, @RequestParam(required=false) String icon, @RequestParam(required=false) String color) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Category created", svc.createCategory(name, icon, color)));
    }
}
