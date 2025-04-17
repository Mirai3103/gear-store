package com.ecom.controller.rest;

import com.ecom.dtos.ReportResponseDTO;
import com.ecom.service.impl.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<ReportResponseDTO> getMonthlyReport(@RequestParam int year) {
        return ResponseEntity.ok(reportService.getReport(year));
    }
}
