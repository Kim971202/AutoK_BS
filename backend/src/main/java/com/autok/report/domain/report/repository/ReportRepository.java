package com.autok.report.domain.report.repository;

import com.autok.report.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
