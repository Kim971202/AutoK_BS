package com.autok.report.domain.report.controller;

import com.autok.report.domain.report.dto.ReportGenerateRequest;
import com.autok.report.domain.report.dto.ReportResponse;
import com.autok.report.domain.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    public ResponseEntity<ReportResponse> generate(@Valid @RequestBody ReportGenerateRequest request) {
        ReportResponse response = reportService.generate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReportResponse>> getHistory(@PathVariable Long projectId) {
        return ResponseEntity.ok(reportService.getHistory(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getById(id));
    }
}
