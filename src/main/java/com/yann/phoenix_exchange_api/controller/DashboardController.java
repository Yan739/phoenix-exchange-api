package com.yann.phoenix_exchange_api.controller;

import com.yann.phoenix_exchange_api.dto.dashboard.*;
import com.yann.phoenix_exchange_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/kpis")
    public ResponseEntity<List<KPIResponseDTO>> getKPIs() {
        return ResponseEntity.ok(dashboardService.getKPIs());
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<RevenueChartDTO> getRevenueChart(
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(dashboardService.getRevenueChart(period));
    }

    @GetMapping("/product-stats")
    public ResponseEntity<ProductStatsDTO> getProductStats() {
        return ResponseEntity.ok(dashboardService.getProductStats());
    }

    @GetMapping("/repair-stats")
    public ResponseEntity<RepairStatsDTO> getRepairStats() {
        return ResponseEntity.ok(dashboardService.getRepairStats());
    }
}