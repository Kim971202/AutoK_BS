package com.autok.report.domain.schedule.controller;

import com.autok.report.domain.schedule.dto.DailyBreakdownResponse;
import com.autok.report.domain.schedule.dto.ScheduleConfirmRequest;
import com.autok.report.domain.schedule.dto.ScheduleParseRequest;
import com.autok.report.domain.schedule.dto.ScheduleResponse;
import com.autok.report.domain.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/parse")
    public ResponseEntity<ScheduleResponse> parse(@Valid @RequestBody ScheduleParseRequest request) {
        return ResponseEntity.ok(scheduleService.parse(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirm(@Valid @RequestBody ScheduleConfirmRequest request) {
        return ResponseEntity.ok(scheduleService.confirm(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ScheduleResponse> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(scheduleService.getByProject(projectId));
    }

    @GetMapping("/{weekId}/daily")
    public ResponseEntity<DailyBreakdownResponse> getDailyBreakdown(@PathVariable Long weekId) {
        return ResponseEntity.ok(scheduleService.getDailyBreakdown(weekId));
    }
}
