package com.autok.report.domain.dailywork.repository;

import com.autok.report.domain.dailywork.entity.DailyWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWorkRepository extends JpaRepository<DailyWork, Long> {

    List<DailyWork> findByProjectIdAndWorkDate(Long projectId, LocalDate workDate);

    List<DailyWork> findByProjectIdOrderByWorkDateDescCreatedAtDesc(Long projectId);

    Optional<DailyWork> findByProjectIdAndUserIdAndWorkDate(Long projectId, Long userId, LocalDate workDate);

    List<DailyWork> findByProjectIdAndWorkDateBetweenOrderByWorkDateAsc(
            Long projectId, LocalDate startDate, LocalDate endDate);
}
