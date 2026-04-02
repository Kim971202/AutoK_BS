import api from './index'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

const MOCK_DAILY_REPORT = `[일일 업무 보고서]
작성일: 2026-04-01 (화)
작성자: 홍길동 / 연구개발팀
프로젝트: AI 기반 문서 분류 시스템

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. 금일 수행 업무

  [데이터 처리]
  - 분류 모델 v2 학습 데이터셋 정제 완료
    . 총 5,200건 데이터 처리
    . 중복 및 노이즈 데이터 제거 (약 300건)

  [버그 수정]
  - 전처리 스크립트 인코딩 오류 수정
    . UTF-8 / EUC-KR 혼용 문제 해결
    . 테스트 케이스 추가 작성

  [회의]
  - 팀 주간 미팅 참석
    . 다음 주 모델 학습 일정 확정
    . API 연동 테스트 범위 논의

2. 특이사항
  - 데이터 정제 과정에서 카테고리 미분류 항목 52건 발견
    → 라벨링 규칙 보완 필요 (내일 처리 예정)

3. 익일 계획
  - 모델 학습 실행 (v2 데이터셋 기반)
  - 미분류 항목 라벨링 규칙 업데이트
  - API 엔드포인트 설계 문서 초안 작성

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`

const MOCK_WEEKLY_REPORT = `[주간 업무 보고서]
보고 기간: 2026-03-30 (월) ~ 2026-04-03 (금)
작성자: 홍길동 / 연구개발팀
프로젝트: AI 기반 문서 분류 시스템

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. 금주 목표 대비 실적

  목표: 모델 최적화 및 API 개발
  달성률: 75%

2. 세부 수행 내용

  [월] 03/30
  - 모델 학습 파이프라인 구축 완료
  - 학습 결과 시각화 대시보드 초안 작성
  - 관련 논문 리뷰 1편 완료

  [화] 03/31
  - 데이터 수집 크롤러 성능 개선 (처리속도 2배 향상)
  - 신규 카테고리 라벨링 규칙 정의
  - 코드 리뷰 2건 진행

  [수] 04/01
  - 분류 모델 v2 학습 데이터셋 정제 완료 (5,200건)
  - 전처리 스크립트 인코딩 오류 수정
  - 팀 주간 미팅 참석

  [목] 04/02
  - 모델 하이퍼파라미터 튜닝 (1차)
  - REST API 엔드포인트 설계 문서 작성

  [금] 04/03
  - 모델 학습 결과 분석 및 리포트 작성
  - 통합 테스트 케이스 설계

3. 이슈 및 리스크
  - 학습 데이터 내 카테고리 미분류 항목 존재 (52건)
    → 라벨링 규칙 보완 완료, 재처리 예정
  - GPU 서버 사용 스케줄 조율 필요

4. 차주 계획
  - 모델 v2 본격 학습 및 성능 평가
  - API 엔드포인트 구현 착수
  - 통합 테스트 환경 구축

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`

const MOCK_REPORT_HISTORY = [
  {
    id: 1,
    projectId: 1,
    type: 'DAILY',
    targetDate: '2026-04-01',
    content: MOCK_DAILY_REPORT,
    createdAt: '2026-04-01T18:30:00',
  },
  {
    id: 2,
    projectId: 1,
    type: 'WEEKLY',
    targetDate: '2026-03-30',
    content: MOCK_WEEKLY_REPORT,
    createdAt: '2026-04-03T17:00:00',
  },
]

let mockIdCounter = 3

export default {
  generate(params) {
    if (USE_MOCK) {
      return new Promise((resolve) =>
        setTimeout(
          () =>
            resolve({
              data: {
                id: mockIdCounter++,
                projectId: params.projectId,
                type: params.type,
                targetDate: params.targetDate,
                content:
                  params.type === 'DAILY'
                    ? MOCK_DAILY_REPORT
                    : MOCK_WEEKLY_REPORT,
                createdAt: new Date().toISOString(),
              },
            }),
          3000
        )
      )
    }
    return api.post('/reports/generate', params)
  },

  getHistory(projectId) {
    if (USE_MOCK) {
      const reports = MOCK_REPORT_HISTORY.filter(
        (r) => r.projectId === Number(projectId)
      )
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: reports }), 500)
      )
    }
    return api.get(`/reports/project/${projectId}`)
  },

  getById(id) {
    if (USE_MOCK) {
      const report = MOCK_REPORT_HISTORY.find((r) => r.id === Number(id))
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: report || null }), 300)
      )
    }
    return api.get(`/reports/${id}`)
  },
}
