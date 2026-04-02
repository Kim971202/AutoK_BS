package com.autok.report.domain.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    private String projectName;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
