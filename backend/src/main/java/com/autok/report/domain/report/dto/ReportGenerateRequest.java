package com.autok.report.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerateRequest {

    @NotNull(message = "프로젝트 ID는 필수입니다.")
    private Long projectId;

    @NotBlank(message = "보고서 유형은 필수입니다.")
    private String type; // DAILY or WEEKLY

    @NotNull(message = "대상 날짜는 필수입니다.")
    private LocalDate targetDate;
}
