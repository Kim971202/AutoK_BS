package com.autok.report.domain.schedule.repository;

import com.autok.report.domain.schedule.entity.WeekSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeekScheduleRepository extends JpaRepository<WeekSchedule, Long> {

    @Query("SELECT DISTINCT ws FROM WeekSchedule ws LEFT JOIN FETCH ws.tasks WHERE ws.project.id = :projectId ORDER BY ws.weekNumber")
    List<WeekSchedule> findByProjectIdWithTasks(@Param("projectId") Long projectId);

    @Query("SELECT ws FROM WeekSchedule ws LEFT JOIN FETCH ws.tasks WHERE ws.id = :weekId")
    Optional<WeekSchedule> findByIdWithTasks(@Param("weekId") Long weekId);

    void deleteByProjectId(Long projectId);
}
