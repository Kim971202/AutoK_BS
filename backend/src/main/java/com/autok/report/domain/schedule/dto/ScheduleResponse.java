package com.autok.report.domain.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private List<WeekDto> weeks;

    public static ScheduleResponse of(List<WeekDto> weeks) {
        return ScheduleResponse.builder().weeks(weeks).build();
    }
}
