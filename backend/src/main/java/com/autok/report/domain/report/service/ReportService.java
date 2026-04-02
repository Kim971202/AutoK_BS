package com.autok.report.domain.report.service;

import com.autok.report.domain.dailywork.entity.DailyWork;
import com.autok.report.domain.dailywork.repository.DailyWorkRepository;
import com.autok.report.domain.project.entity.Project;
import com.autok.report.domain.project.repository.ProjectRepository;
import com.autok.report.domain.report.dto.ReportGenerateRequest;
import com.autok.report.domain.report.dto.ReportResponse;
import com.autok.report.domain.report.entity.Report;
import com.autok.report.domain.report.repository.ReportRepository;
import com.autok.report.global.claude.ClaudeApiClient;
import com.autok.report.global.claude.PromptBuilder;
import com.autok.report.global.exception.BusinessException;
import com.autok.report.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final DailyWorkRepository dailyWorkRepository;
    private final ProjectRepository projectRepository;
    private final ClaudeApiClient claudeApiClient;
    private final PromptBuilder promptBuilder;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional
    public ReportResponse generate(ReportGenerateRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트", request.getProjectId()));

        String content;
        if ("DAILY".equalsIgnoreCase(request.getType())) {
            content = generateDailyReport(project, request.getTargetDate());
        } else if ("WEEKLY".equalsIgnoreCase(request.getType())) {
            content = generateWeeklyReport(project, request.getTargetDate());
        } else {
            throw new BusinessException("보고서 유형은 DAILY 또는 WEEKLY만 가능합니다.");
        }

        Report report = Report.builder()
                .projectId(project.getId())
                .type(request.getType().toUpperCase())
                .targetDate(request.getTargetDate())
                .content(content)
                .build();

        Report saved = reportRepository.save(report);
        return ReportResponse.from(saved);
    }

    public List<ReportResponse> getHistory(Long projectId) {
        return reportRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(ReportResponse::from)
                .toList();
    }

    public ReportResponse getById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("보고서", id));
        return ReportResponse.from(report);
    }

    private String generateDailyReport(Project project, LocalDate date) {
        List<DailyWork> works = dailyWorkRepository.findByProjectIdAndWorkDate(project.getId(), date);

        if (works.isEmpty()) {
            throw new BusinessException("해당 날짜에 등록된 작업 내역이 없습니다.");
        }

        String workContent = works.stream()
                .map(DailyWork::getContent)
                .collect(Collectors.joining("\n"));

        String prompt = promptBuilder.buildDailyReportPrompt(
                project.getProjectName(),
                date.format(DATE_FMT),
                workContent
        );

        return claudeApiClient.generate(prompt);
    }

    private String generateWeeklyReport(Project project, LocalDate weekStart) {
        // weekStart is Monday; find Friday
        LocalDate weekEnd = weekStart.plusDays(4);

        List<DailyWork> works = dailyWorkRepository
                .findByProjectIdAndWorkDateBetweenOrderByWorkDateAsc(project.getId(), weekStart, weekEnd);

        if (works.isEmpty()) {
            throw new BusinessException("해당 주차에 등록된 작업 내역이 없습니다.");
        }

        List<String> workContents = works.stream()
                .map(w -> "[" + w.getWorkDate().format(DATE_FMT) + "] " + w.getContent())
                .toList();

        String prompt = promptBuilder.buildWeeklyReportPrompt(
                project.getProjectName(),
                weekStart.format(DATE_FMT),
                weekEnd.format(DATE_FMT),
                workContents
        );

        return claudeApiClient.generate(prompt);
    }
}
