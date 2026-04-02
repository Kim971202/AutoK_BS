package com.autok.report.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeekDto {

    private int weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private List<TaskDto> tasks;
}
