package com.autok.report.domain.dailywork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyWorkRequest {

    @NotNull(message = "프로젝트 ID는 필수입니다.")
    private Long projectId;

    @NotNull(message = "작업 날짜는 필수입니다.")
    private LocalDate workDate;

    @NotBlank(message = "작업 내용은 필수입니다.")
    private String content;
}
