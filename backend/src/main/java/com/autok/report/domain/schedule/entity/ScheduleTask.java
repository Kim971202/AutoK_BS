package com.autok.report.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_task")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScheduleTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_schedule_id", nullable = false)
    private WeekSchedule weekSchedule;

    @Column(nullable = false, length = 500)
    private String content;

    @Builder.Default
    private boolean completed = false;

    @Column(name = "day_of_week", length = 20)
    private String dayOfWeek;
}
