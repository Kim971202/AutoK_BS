package com.autok.report.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBreakdownResponse {

    private Long weekId;
    private int weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private Map<String, List<TaskDto>> dailyTasks;
}
