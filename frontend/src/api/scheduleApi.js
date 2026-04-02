import api from './index'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

const MOCK_PARSED_SCHEDULE = {
  weeks: [
    {
      weekNumber: 1,
      weekId: 'w1',
      startDate: '2026-03-02',
      endDate: '2026-03-06',
      goal: '프로젝트 킥오프 및 환경 구성',
      tasks: [
        { id: 't1', content: '프로젝트 요구사항 분석 및 정리', completed: false, dayOfWeek: 'MONDAY' },
        { id: 't2', content: '개발 환경 세팅 (IDE, Git, CI/CD)', completed: false, dayOfWeek: 'TUESDAY' },
        { id: 't3', content: '기술 스택 선정 및 아키텍처 설계', completed: false, dayOfWeek: 'WEDNESDAY' },
      ],
    },
    {
      weekNumber: 2,
      weekId: 'w2',
      startDate: '2026-03-09',
      endDate: '2026-03-13',
      goal: '데이터 수집 및 전처리 파이프라인 구축',
      tasks: [
        { id: 't4', content: '학습 데이터 수집 및 정제', completed: false, dayOfWeek: 'MONDAY' },
        { id: 't5', content: '데이터 전처리 스크립트 작성', completed: false, dayOfWeek: 'WEDNESDAY' },
        { id: 't6', content: '데이터 품질 검증 모듈 개발', completed: false, dayOfWeek: 'FRIDAY' },
      ],
    },
    {
      weekNumber: 3,
      weekId: 'w3',
      startDate: '2026-03-16',
      endDate: '2026-03-20',
      goal: '모델 개발 및 초기 학습',
      tasks: [
        { id: 't7', content: '분류 모델 설계 및 구현', completed: false, dayOfWeek: 'MONDAY' },
        { id: 't8', content: '초기 학습 데이터 준비 및 학습 실행', completed: false, dayOfWeek: 'TUESDAY' },
        { id: 't9', content: '모델 성능 평가 지표 설정', completed: false, dayOfWeek: 'THURSDAY' },
      ],
    },
    {
      weekNumber: 4,
      weekId: 'w4',
      startDate: '2026-03-23',
      endDate: '2026-03-27',
      goal: '모델 최적화 및 API 개발',
      tasks: [
        { id: 't10', content: '모델 하이퍼파라미터 튜닝', completed: false, dayOfWeek: 'MONDAY' },
        { id: 't11', content: 'REST API 엔드포인트 개발', completed: false, dayOfWeek: 'WEDNESDAY' },
        { id: 't12', content: '통합 테스트 작성', completed: false, dayOfWeek: 'FRIDAY' },
      ],
    },
  ],
}

const MOCK_DAILY_SCHEDULES = {
  w1: {
    weekId: 'w1',
    days: {
      MONDAY: [
        { id: 'd1_1', content: '프로젝트 요구사항 분석 및 정리' },
        { id: 'd1_2', content: '이해관계자 미팅 및 요구사항 확인' },
      ],
      TUESDAY: [
        { id: 'd1_3', content: '개발 환경 세팅 (IDE, Git, CI/CD)' },
        { id: 'd1_4', content: 'Git 브랜치 전략 수립' },
      ],
      WEDNESDAY: [
        { id: 'd1_5', content: '기술 스택 선정 및 아키텍처 설계' },
      ],
      THURSDAY: [
        { id: 'd1_6', content: '프로토타입 구조 설계' },
        { id: 'd1_7', content: 'DB 스키마 초안 작성' },
      ],
      FRIDAY: [
        { id: 'd1_8', content: '주간 회의 및 1주차 리뷰' },
      ],
    },
  },
  w2: {
    weekId: 'w2',
    days: {
      MONDAY: [
        { id: 'd2_1', content: '학습 데이터 소스 조사' },
        { id: 'd2_2', content: '데이터 수집 스크립트 작성' },
      ],
      TUESDAY: [
        { id: 'd2_3', content: '수집 데이터 탐색적 분석(EDA)' },
      ],
      WEDNESDAY: [
        { id: 'd2_4', content: '데이터 전처리 스크립트 작성' },
        { id: 'd2_5', content: '결측치 및 이상치 처리 로직 구현' },
      ],
      THURSDAY: [
        { id: 'd2_6', content: '데이터 파이프라인 자동화 설정' },
      ],
      FRIDAY: [
        { id: 'd2_7', content: '데이터 품질 검증 모듈 개발' },
        { id: 'd2_8', content: '2주차 진행 상황 정리' },
      ],
    },
  },
  w3: {
    weekId: 'w3',
    days: {
      MONDAY: [
        { id: 'd3_1', content: '분류 모델 아키텍처 설계' },
      ],
      TUESDAY: [
        { id: 'd3_2', content: '모델 구현 (PyTorch/TensorFlow)' },
        { id: 'd3_3', content: '초기 학습 데이터셋 분할' },
      ],
      WEDNESDAY: [
        { id: 'd3_4', content: '초기 학습 실행 및 로그 분석' },
      ],
      THURSDAY: [
        { id: 'd3_5', content: '모델 성능 평가 지표 설정' },
        { id: 'd3_6', content: '베이스라인 모델 성능 측정' },
      ],
      FRIDAY: [
        { id: 'd3_7', content: '3주차 결과 정리 및 리뷰' },
      ],
    },
  },
  w4: {
    weekId: 'w4',
    days: {
      MONDAY: [
        { id: 'd4_1', content: '하이퍼파라미터 튜닝 실험 설계' },
        { id: 'd4_2', content: '그리드 서치 / 랜덤 서치 실행' },
      ],
      TUESDAY: [
        { id: 'd4_3', content: '최적 모델 선정 및 검증' },
      ],
      WEDNESDAY: [
        { id: 'd4_4', content: 'REST API 엔드포인트 설계' },
        { id: 'd4_5', content: 'API 구현 (FastAPI/Spring Boot)' },
      ],
      THURSDAY: [
        { id: 'd4_6', content: '통합 테스트 작성' },
        { id: 'd4_7', content: 'API 문서화 (Swagger)' },
      ],
      FRIDAY: [
        { id: 'd4_8', content: '최종 리뷰 및 배포 준비' },
      ],
    },
  },
}

export default {
  parse(projectName, startDate, endDate, description) {
    if (USE_MOCK) {
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: MOCK_PARSED_SCHEDULE }), 3000)
      )
    }
    return api.post('/schedules/parse', { projectName, startDate, endDate, description })
  },

  confirm(projectId, weeks) {
    if (USE_MOCK) {
      return new Promise((resolve) =>
        setTimeout(
          () =>
            resolve({
              data: { success: true, projectId, weekCount: weeks.length },
            }),
          500
        )
      )
    }
    return api.post('/schedules/confirm', { projectId, weeks })
  },

  getByProject(projectId) {
    if (USE_MOCK) {
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: MOCK_PARSED_SCHEDULE }), 500)
      )
    }
    return api.get(`/schedules/project/${projectId}`)
  },

  getDailySchedule(weekId) {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        const data = MOCK_DAILY_SCHEDULES[weekId] || { weekId, days: {} }
        setTimeout(() => resolve({ data }), 500)
      })
    }
    return api.get(`/schedules/${weekId}/daily`)
  },
}
