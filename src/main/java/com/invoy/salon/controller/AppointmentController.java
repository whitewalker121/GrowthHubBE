package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.AppointmentServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/v1/appointments") @RequiredArgsConstructor @Tag(name = "Appointments")
public class AppointmentController {
    private final AppointmentServiceImpl apptService;
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> create(@Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Appointment booked", apptService.create(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> list(
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        if (date != null) return ResponseEntity.ok(ApiResponse.ok(apptService.listByDate(date)));
        LocalDate f = from != null ? from : LocalDate.now();
        LocalDate t = to != null ? to : f.plusDays(30);
        return ResponseEntity.ok(ApiResponse.ok(apptService.listByDateRange(f, t)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(apptService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Appointment updated", apptService.update(id, req)));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated", apptService.updateStatus(id, status)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        apptService.cancel(id); return ResponseEntity.ok(ApiResponse.ok("Appointment cancelled"));
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> byClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(ApiResponse.ok(apptService.listByClient(clientId)));
    }
}
