package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.AttendanceServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/v1/attendance") @RequiredArgsConstructor @Tag(name = "Attendance")
public class AttendanceController {
    private final AttendanceServiceImpl attendanceService;
    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(@Valid @RequestBody AttendanceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Attendance marked", attendanceService.markAttendance(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> listByDate(
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.listByDate(date != null ? date : LocalDate.now())));
    }
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> listByStaff(
        @PathVariable Long staffId,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(attendanceService.listByStaff(staffId, f, t)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Attendance updated", attendanceService.update(id, req)));
    }
}
