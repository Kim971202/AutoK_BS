package com.autok.report.domain.dailywork.dto;

import com.autok.report.domain.dailywork.entity.DailyWork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyWorkResponse {

    private Long id;
    private Long projectId;
    private Long userId;
    private LocalDate workDate;
    private String content;
    private LocalDateTime createdAt;

    public static DailyWorkResponse from(DailyWork dw) {
        return DailyWorkResponse.builder()
                .id(dw.getId())
                .projectId(dw.getProjectId())
                .userId(dw.getUserId())
                .workDate(dw.getWorkDate())
                .content(dw.getContent())
                .createdAt(dw.getCreatedAt())
                .build();
    }
}
