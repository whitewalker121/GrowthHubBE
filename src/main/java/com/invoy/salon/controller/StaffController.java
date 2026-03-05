package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.StaffServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/staff") @RequiredArgsConstructor @Tag(name = "Staff")
public class StaffController {
    private final StaffServiceImpl staffService;
    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> create(@Valid @RequestBody StaffRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Staff created", staffService.create(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffResponse>>> list(@RequestParam(required=false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.list(status)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> update(@PathVariable Long id, @Valid @RequestBody StaffRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Staff updated", staffService.update(id, req)));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        staffService.updateStatus(id, status); return ResponseEntity.ok(ApiResponse.ok("Status updated"));
    }
}
