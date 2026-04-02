package com.autok.report.domain.dailywork.service;

import com.autok.report.domain.dailywork.dto.DailyWorkRequest;
import com.autok.report.domain.dailywork.dto.DailyWorkResponse;
import com.autok.report.domain.dailywork.entity.DailyWork;
import com.autok.report.domain.dailywork.repository.DailyWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyWorkService {

    private final DailyWorkRepository dailyWorkRepository;

    public List<DailyWorkResponse> getByProjectAndDate(Long projectId, LocalDate date) {
        return dailyWorkRepository.findByProjectIdAndWorkDate(projectId, date)
                .stream()
                .map(DailyWorkResponse::from)
                .toList();
    }

    public List<DailyWorkResponse> getByProject(Long projectId) {
        return dailyWorkRepository.findByProjectIdOrderByWorkDateDescCreatedAtDesc(projectId)
                .stream()
                .map(DailyWorkResponse::from)
                .toList();
    }

    /**
     * UPSERT: if entry exists for same project+user+date, update content; otherwise create new.
     */
    @Transactional
    public DailyWorkResponse save(DailyWorkRequest request) {
        Long userId = 1L; // default user

        DailyWork dailyWork = dailyWorkRepository
                .findByProjectIdAndUserIdAndWorkDate(request.getProjectId(), userId, request.getWorkDate())
                .map(existing -> {
                    existing.updateContent(request.getContent());
                    return existing;
                })
                .orElseGet(() -> DailyWork.builder()
                        .projectId(request.getProjectId())
                        .userId(userId)
                        .workDate(request.getWorkDate())
                        .content(request.getContent())
                        .build());

        DailyWork saved = dailyWorkRepository.save(dailyWork);
        return DailyWorkResponse.from(saved);
    }
}
