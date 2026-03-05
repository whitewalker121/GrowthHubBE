package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.ExpenseServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/v1/expenses") @RequiredArgsConstructor @Tag(name = "Expenses")
public class ExpenseController {
    private final ExpenseServiceImpl expenseService;
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Expense recorded", expenseService.create(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> list(
        @RequestParam(required=false) String category,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.list(category, from, to)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Expense updated", expenseService.update(id, req)));
    }
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ExpenseResponse>> approve(@PathVariable Long id, @RequestParam Long approverStaffId) {
        return ResponseEntity.ok(ApiResponse.ok("Expense approved", expenseService.approve(id, approverStaffId)));
    }
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ExpenseResponse>> reject(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Expense rejected", expenseService.reject(id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseService.delete(id); return ResponseEntity.ok(ApiResponse.ok("Expense deleted"));
    }
}
