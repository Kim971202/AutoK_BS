package com.autok.report.domain.dailywork.controller;

import com.autok.report.domain.dailywork.dto.DailyWorkRequest;
import com.autok.report.domain.dailywork.dto.DailyWorkResponse;
import com.autok.report.domain.dailywork.service.DailyWorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-works")
@RequiredArgsConstructor
public class DailyWorkController {

    private final DailyWorkService dailyWorkService;

    @GetMapping
    public ResponseEntity<List<DailyWorkResponse>> getByProjectAndDate(
            @RequestParam Long projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(dailyWorkService.getByProjectAndDate(projectId, date));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<DailyWorkResponse>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(dailyWorkService.getByProject(projectId));
    }

    @PostMapping
    public ResponseEntity<DailyWorkResponse> save(@Valid @RequestBody DailyWorkRequest request) {
        DailyWorkResponse response = dailyWorkService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
