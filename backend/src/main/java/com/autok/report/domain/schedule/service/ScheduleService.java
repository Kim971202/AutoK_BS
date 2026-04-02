package com.autok.report.domain.schedule.service;

import com.autok.report.domain.project.entity.Project;
import com.autok.report.domain.project.repository.ProjectRepository;
import com.autok.report.domain.schedule.dto.*;
import com.autok.report.domain.schedule.entity.ScheduleTask;
import com.autok.report.domain.schedule.entity.WeekSchedule;
import com.autok.report.domain.schedule.repository.WeekScheduleRepository;
import com.autok.report.global.claude.ClaudeApiClient;
import com.autok.report.global.claude.PromptBuilder;
import com.autok.report.global.exception.BusinessException;
import com.autok.report.global.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final WeekScheduleRepository weekScheduleRepository;
    private final ProjectRepository projectRepository;
    private final ClaudeApiClient claudeApiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Parse raw text into structured schedule using Claude API.
     */
    public ScheduleResponse parse(ScheduleParseRequest request) {
        // Verify project exists only if projectId is provided
        if (request.getProjectId() != null) {
            projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("프로젝트", request.getProjectId()));
        }

        // Build prompt: use project info if rawText is blank
        String prompt;
        if (request.getRawText() == null || request.getRawText().isBlank()) {
            if (request.getProjectName() == null || request.getStartDate() == null || request.getEndDate() == null) {
                throw new BusinessException("일정 생성을 위해 프로젝트명, 시작일, 종료일이 필요합니다.");
            }
            prompt = promptBuilder.buildScheduleFromProjectPrompt(
                    request.getProjectName(), request.getDescription(),
                    request.getStartDate(), request.getEndDate());
        } else {
            prompt = promptBuilder.buildScheduleParsePrompt(request.getRawText());
        }

        String claudeResponse = claudeApiClient.generate(prompt);

        // Parse Claude's JSON response
        List<WeekDto> weeks = parseClaudeScheduleResponse(claudeResponse);

        return ScheduleResponse.of(weeks);
    }

    /**
     * Confirm and save schedule to DB.
     */
    @Transactional
    public Map<String, Object> confirm(ScheduleConfirmRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트", request.getProjectId()));

        // Delete existing schedules for this project
        weekScheduleRepository.deleteByProjectId(project.getId());
        weekScheduleRepository.flush();

        AtomicInteger totalTasks = new AtomicInteger(0);

        for (WeekDto weekDto : request.getWeeks()) {
            WeekSchedule schedule = WeekSchedule.builder()
                    .project(project)
                    .weekNumber(weekDto.getWeekNumber())
                    .startDate(weekDto.getStartDate())
                    .endDate(weekDto.getEndDate())
                    .goal(weekDto.getGoal())
                    .build();

            if (weekDto.getTasks() != null) {
                for (TaskDto taskDto : weekDto.getTasks()) {
                    ScheduleTask task = ScheduleTask.builder()
                            .content(taskDto.getContent())
                            .completed(taskDto.isCompleted())
                            .dayOfWeek(taskDto.getDayOfWeek())
                            .build();
                    schedule.addTask(task);
                    totalTasks.incrementAndGet();
                }
            }

            weekScheduleRepository.save(schedule);
        }

        return Map.of(
                "success", true,
                "projectId", project.getId(),
                "weekCount", request.getWeeks().size(),
                "totalTasks", totalTasks.get()
        );
    }

    /**
     * Get schedule for a project.
     */
    public ScheduleResponse getByProject(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트", projectId));

        List<WeekSchedule> schedules = weekScheduleRepository.findByProjectIdWithTasks(projectId);

        List<WeekDto> weeks = schedules.stream()
                .map(ws -> WeekDto.builder()
                        .weekNumber(ws.getWeekNumber())
                        .startDate(ws.getStartDate())
                        .endDate(ws.getEndDate())
                        .goal(ws.getGoal())
                        .tasks(ws.getTasks().stream()
                                .map(t -> TaskDto.builder()
                                        .id(String.valueOf(t.getId()))
                                        .content(t.getContent())
                                        .completed(t.isCompleted())
                                        .dayOfWeek(t.getDayOfWeek())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return ScheduleResponse.of(weeks);
    }

    /**
     * Get daily breakdown for a specific week schedule.
     */
    public DailyBreakdownResponse getDailyBreakdown(Long weekId) {
        WeekSchedule weekSchedule = weekScheduleRepository.findByIdWithTasks(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("주차 일정", weekId));

        List<String> days = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
        Map<String, List<TaskDto>> dailyTasks = new LinkedHashMap<>();

        for (String day : days) {
            dailyTasks.put(day, new ArrayList<>());
        }

        for (ScheduleTask task : weekSchedule.getTasks()) {
            TaskDto dto = TaskDto.builder()
                    .id(String.valueOf(task.getId()))
                    .content(task.getContent())
                    .completed(task.isCompleted())
                    .dayOfWeek(task.getDayOfWeek())
                    .build();

            String day = task.getDayOfWeek();
            if (day != null && dailyTasks.containsKey(day)) {
                dailyTasks.get(day).add(dto);
            } else {
                // Tasks without a specific day go into MONDAY by default
                dailyTasks.get("MONDAY").add(dto);
            }
        }

        return DailyBreakdownResponse.builder()
                .weekId(weekSchedule.getId())
                .weekNumber(weekSchedule.getWeekNumber())
                .startDate(weekSchedule.getStartDate())
                .endDate(weekSchedule.getEndDate())
                .goal(weekSchedule.getGoal())
                .dailyTasks(dailyTasks)
                .build();
    }

    /**
     * Parse Claude's JSON response into a list of WeekDto.
     * Uses defensive parsing: tries ```json block -> ``` block -> { } range -> whole text.
     */
    private List<WeekDto> parseClaudeScheduleResponse(String response) {
        String json = extractJson(response);

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode weeksNode = root.has("weeks") ? root.get("weeks") : root;

            if (!weeksNode.isArray()) {
                throw new BusinessException("일정 파싱 결과가 올바른 형식이 아닙니다.");
            }

            List<WeekDto> weeks = new ArrayList<>();
            for (JsonNode weekNode : weeksNode) {
                List<TaskDto> tasks = new ArrayList<>();
                JsonNode tasksNode = weekNode.get("tasks");
                if (tasksNode != null && tasksNode.isArray()) {
                    for (JsonNode taskNode : tasksNode) {
                        String title = taskNode.has("title") ? taskNode.get("title").asText() : "";
                        String description = taskNode.has("description") ? taskNode.get("description").asText() : "";
                        String content = title;
                        if (!description.isEmpty()) {
                            content = title + " - " + description;
                        }
                        String dayOfWeek = taskNode.has("dayOfWeek") ? taskNode.get("dayOfWeek").asText() : null;

                        tasks.add(TaskDto.builder()
                                .id(UUID.randomUUID().toString().substring(0, 8))
                                .content(content)
                                .completed(false)
                                .dayOfWeek(dayOfWeek)
                                .build());
                    }
                }

                String startDateStr = weekNode.has("startDate") ? weekNode.get("startDate").asText() : null;
                String endDateStr = weekNode.has("endDate") ? weekNode.get("endDate").asText() : null;

                weeks.add(WeekDto.builder()
                        .weekNumber(weekNode.has("weekNumber") ? weekNode.get("weekNumber").asInt() : weeks.size() + 1)
                        .startDate(startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now().plusWeeks(weeks.size()))
                        .endDate(endDateStr != null ? LocalDate.parse(endDateStr) : LocalDate.now().plusWeeks(weeks.size()).plusDays(4))
                        .goal(weekNode.has("goal") ? weekNode.get("goal").asText() : "")
                        .tasks(tasks)
                        .build());
            }

            if (weeks.isEmpty()) {
                throw new BusinessException("일정 파싱 결과가 비어있습니다. 입력 텍스트를 확인해 주세요.");
            }

            return weeks;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Claude schedule response: {}", response, e);
            throw new BusinessException("Claude 응답을 JSON으로 파싱할 수 없습니다. 입력 텍스트를 다시 확인해 주세요.");
        }
    }

    /**
     * Extract JSON string from Claude's response using defensive parsing.
     * Order: ```json block -> ``` block -> { } range -> whole text.
     */
    private String extractJson(String response) {
        if (response == null || response.isBlank()) {
            throw new BusinessException("Claude API로부터 빈 응답을 받았습니다.");
        }

        String trimmed = response.trim();

        // Try ```json ... ``` block
        int jsonStart = trimmed.indexOf("```json");
        if (jsonStart >= 0) {
            int contentStart = trimmed.indexOf('\n', jsonStart);
            int blockEnd = trimmed.indexOf("```", contentStart + 1);
            if (contentStart >= 0 && blockEnd > contentStart) {
                return trimmed.substring(contentStart + 1, blockEnd).trim();
            }
        }

        // Try ``` ... ``` block
        int backticksStart = trimmed.indexOf("```");
        if (backticksStart >= 0) {
            int contentStart = trimmed.indexOf('\n', backticksStart);
            int blockEnd = trimmed.indexOf("```", contentStart + 1);
            if (contentStart >= 0 && blockEnd > contentStart) {
                return trimmed.substring(contentStart + 1, blockEnd).trim();
            }
        }

        // Try { ... } range
        int braceStart = trimmed.indexOf('{');
        int braceEnd = trimmed.lastIndexOf('}');
        if (braceStart >= 0 && braceEnd > braceStart) {
            return trimmed.substring(braceStart, braceEnd + 1);
        }

        // Fall back to whole text
        return trimmed;
    }
}
