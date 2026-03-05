package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.InvoiceServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/v1/invoices") @RequiredArgsConstructor @Tag(name = "Invoices / POS")
public class InvoiceController {
    private final InvoiceServiceImpl invoiceService;
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(@Valid @RequestBody InvoiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Invoice created", invoiceService.create(req)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.getById(id)));
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> byClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.listByClient(clientId)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> byDateRange(
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.listByDateRange(f, t)));
    }
}
