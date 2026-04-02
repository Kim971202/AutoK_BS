package com.autok.report.domain.project.service;

import com.autok.report.domain.project.dto.ProjectRequest;
import com.autok.report.domain.project.dto.ProjectResponse;
import com.autok.report.domain.project.entity.Project;
import com.autok.report.domain.project.repository.ProjectRepository;
import com.autok.report.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectResponse> findAll() {
        return projectRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse findById(Long id) {
        Project project = getProjectOrThrow(id);
        return ProjectResponse.from(project);
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = Project.builder()
                .projectName(request.getProjectName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : "IN_PROGRESS")
                .progress(0)
                .build();
        Project saved = projectRepository.save(project);
        return ProjectResponse.from(saved);
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProjectOrThrow(id);
        project.update(
                request.getProjectName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
        return ProjectResponse.from(project);
    }

    @Transactional
    public void delete(Long id) {
        Project project = getProjectOrThrow(id);
        projectRepository.delete(project);
    }

    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("프로젝트", id));
    }
}
