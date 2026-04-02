package com.autok.report.domain.report.dto;

import com.autok.report.domain.report.entity.Report;
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
public class ReportResponse {

    private Long id;
    private Long projectId;
    private String type;
    private LocalDate targetDate;
    private String content;
    private LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .projectId(report.getProjectId())
                .type(report.getType())
                .targetDate(report.getTargetDate())
                .content(report.getContent())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
