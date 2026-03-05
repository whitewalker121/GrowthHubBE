package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.DashboardServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/dashboard") @RequiredArgsConstructor @Tag(name = "Dashboard")
public class DashboardController {
    private final DashboardServiceImpl dashboardService;
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStats>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getDashboardStats()));
    }
}
