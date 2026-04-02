package com.autok.report.global.claude;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class PromptBuilder {

    /**
     * Build prompt to parse free-form / conversational Korean schedule text into structured JSON
     * with daily granularity (Mon-Fri) within each week.
     */
    public String buildScheduleParsePrompt(String scheduleText) {
        return """
                당신은 프로젝트 일정 파서입니다. 사용자가 구두체(대화체) 한국어로 입력한 일정을 분석하여 주차별·요일별 일정 JSON으로 변환해 주세요.

                입력 특징:
                - 사용자는 "월요일에 백엔드 API 시작하고 수요일까지 완료할 예정이에요" 같은 자연스러운 구어체를 사용합니다.
                - "주 중반쯤", "이번 주 안에", "금요일까지는" 등의 모호한 표현도 가능합니다.
                - "~하고", "~한 다음에", "그 후에" 등의 연결 표현으로 여러 작업을 나열할 수 있습니다.

                파싱 규칙:
                1. 텍스트에서 주차, 기간, 요일별 업무 내용을 추출하세요.
                2. 날짜가 명시되어 있으면 그대로 사용하고, 없으면 합리적으로 추정하세요.
                3. 각 task에 dayOfWeek를 반드시 지정하세요. 허용 값: "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"
                4. 요일이 명확하지 않은 경우 다음 규칙을 따르세요:
                   - "주 초" → MONDAY
                   - "주 중반" → WEDNESDAY
                   - "주 후반", "주말쯤" → FRIDAY
                   - "이번 주 안에", "한 주 내내" → 해당 작업을 MONDAY에 시작으로 배치
                   - "~부터 ~까지" → 시작 요일에 배치하되 description에 기간 명시
                5. 각 주차에는 최소 1개 이상의 task가 있어야 합니다.
                6. goal은 해당 주차의 주요 목표를 자연스러운 한국어 한 줄로 요약하세요.
                7. title과 description은 자연스러운 한국어로 작성하세요.
                8. 반드시 아래 JSON 형식만 반환하세요. 다른 설명이나 텍스트는 포함하지 마세요.

                출력 JSON 형식:
                ```json
                {
                  "weeks": [
                    {
                      "weekNumber": 1,
                      "startDate": "2024-04-07",
                      "endDate": "2024-04-11",
                      "goal": "주차 목표 한 줄 요약",
                      "tasks": [
                        {"dayOfWeek": "MONDAY", "title": "작업명", "description": "세부 내용"},
                        {"dayOfWeek": "WEDNESDAY", "title": "작업명", "description": "세부 내용"}
                      ]
                    }
                  ]
                }
                ```

                분석할 텍스트:
                ---
                %s
                ---

                위 텍스트를 분석하여 JSON만 반환하세요.
                """.formatted(scheduleText);
    }

    /**
     * Build prompt to generate a formal Korean daily report.
     */
    public String buildDailyReportPrompt(String projectName, String date, String workContent) {
        return """
                당신은 건설/IT 프로젝트의 업무 보고서 작성 전문가입니다.
                아래 정보를 바탕으로 공식적인 일일 업무 보고서(일보)를 작성해 주세요.

                프로젝트명: %s
                작성일: %s
                수행한 업무 내용:
                %s

                작성 규칙:
                1. 공식적이고 전문적인 한국어로 작성하세요.
                2. 업무 내용을 체계적으로 정리하고, 가능한 경우 카테고리별로 분류하세요.
                3. 진행률이나 완료 상태를 포함하세요.
                4. 특이사항이나 이슈가 있다면 별도 섹션으로 분리하세요.
                5. 다음 형식을 따라주세요:

                [일일 업무 보고서]
                작성일: (날짜)
                프로젝트: (프로젝트명)

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                1. 금일 수행 업무
                   (체계적으로 정리된 업무 내용)

                2. 특이사항 / 이슈
                   (있는 경우)

                3. 익일 계획
                   (업무 내용을 바탕으로 추정)

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                보고서 본문만 작성해 주세요. 추가 설명은 포함하지 마세요.
                """.formatted(projectName, date, workContent);
    }

    /**
     * Build prompt to generate a formal Korean weekly report.
     */
    public String buildWeeklyReportPrompt(String projectName, String startDate, String endDate,
                                           List<String> workContents) {
        String allContents = String.join("\n\n", workContents);

        return """
                당신은 건설/IT 프로젝트의 업무 보고서 작성 전문가입니다.
                아래 정보를 바탕으로 공식적인 주간 업무 보고서(주보)를 작성해 주세요.

                프로젝트명: %s
                보고 기간: %s ~ %s
                주간 수행 업무 내용:
                %s

                작성 규칙:
                1. 공식적이고 전문적인 한국어로 작성하세요.
                2. 일별 업무를 종합하여 주요 성과 위주로 요약하세요.
                3. 주간 진행률과 목표 대비 달성도를 포함하세요.
                4. 다음 주 계획도 업무 내용을 바탕으로 추정하여 포함하세요.
                5. 다음 형식을 따라주세요:

                [주간 업무 보고서]
                보고 기간: (시작일) ~ (종료일)
                프로젝트: (프로젝트명)

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                1. 주요 성과
                   (주간 핵심 성과 요약)

                2. 세부 수행 내용
                   (일별 또는 카테고리별 상세 내용)

                3. 이슈 및 리스크
                   (있는 경우)

                4. 차주 계획
                   (다음 주 예정 업무)

                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                보고서 본문만 작성해 주세요. 추가 설명은 포함하지 마세요.
                """.formatted(projectName, startDate, endDate, allContents);
    }

    /**
     * Build prompt to auto-generate a weekly schedule from project information.
     */
    public String buildScheduleFromProjectPrompt(String projectName, String description,
                                                  LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalWeeks = (totalDays + 6) / 7;

        return """
                당신은 프로젝트 일정 계획 전문가입니다. 아래 프로젝트 정보를 바탕으로 주차별 일정을 생성해 주세요.

                프로젝트명: %s
                설명: %s
                시작일: %s
                종료일: %s
                총 기간: 약 %d주

                작성 규칙:
                1. 시작일부터 종료일까지 전체 기간을 주차로 나눠 계획을 수립하세요.
                2. 각 주차의 startDate는 월요일, endDate는 금요일로 설정하세요. 단, 1주차 startDate는 프로젝트 시작일로 설정하세요.
                3. 각 주차마다 goal(주차 목표)과 tasks(세부 작업)를 작성하세요.
                4. tasks의 dayOfWeek는 "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" 중 하나여야 합니다.
                5. 일반적인 프로젝트 진행 순서(분석 → 설계 → 개발 → 테스트 → 완료)를 고려하세요.
                6. goal은 한국어 한 줄로 요약하세요.
                7. 반드시 아래 JSON 형식만 반환하세요. 다른 설명이나 텍스트는 포함하지 마세요.

                출력 JSON 형식:
                ```json
                {
                  "weeks": [
                    {
                      "weekNumber": 1,
                      "startDate": "2024-04-07",
                      "endDate": "2024-04-11",
                      "goal": "주차 목표 한 줄 요약",
                      "tasks": [
                        {"dayOfWeek": "MONDAY", "title": "작업명", "description": "세부 내용"},
                        {"dayOfWeek": "WEDNESDAY", "title": "작업명", "description": "세부 내용"}
                      ]
                    }
                  ]
                }
                ```

                위 프로젝트에 맞는 주차별 일정 JSON만 반환하세요.
                """.formatted(projectName, description != null ? description : "없음",
                startDate.toString(), endDate.toString(), totalWeeks);
    }
}
