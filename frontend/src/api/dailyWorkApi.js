import api from './index'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

const MOCK_DAILY_WORKS = [
  {
    id: 1,
    projectId: 1,
    userId: 1,
    workDate: '2026-04-01',
    content:
      '- 분류 모델 v2 학습 데이터셋 정제 완료 (총 5,200건)\n- 전처리 스크립트 오류 수정 (인코딩 이슈)\n- 팀 미팅: 다음 주 일정 논의',
    createdAt: '2026-04-01T18:00:00',
  },
  {
    id: 2,
    projectId: 1,
    userId: 1,
    workDate: '2026-03-31',
    content:
      '- 데이터 수집 크롤러 성능 개선 (처리속도 2배 향상)\n- 신규 카테고리 라벨링 규칙 정의\n- 코드 리뷰 2건 진행',
    createdAt: '2026-03-31T17:30:00',
  },
  {
    id: 3,
    projectId: 1,
    userId: 1,
    workDate: '2026-03-30',
    content:
      '- 모델 학습 파이프라인 구축 완료\n- 학습 결과 시각화 대시보드 초안 작성\n- 관련 논문 리뷰 1편 완료',
    createdAt: '2026-03-30T17:00:00',
  },
]

let mockIdCounter = 4

export default {
  getByProjectAndDate(projectId, date) {
    if (USE_MOCK) {
      const works = MOCK_DAILY_WORKS.filter(
        (w) => w.projectId === Number(projectId) && w.workDate === date
      )
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: works }), 300)
      )
    }
    return api.get(`/daily-works`, { params: { projectId, date } })
  },

  getByProject(projectId) {
    if (USE_MOCK) {
      const works = MOCK_DAILY_WORKS.filter(
        (w) => w.projectId === Number(projectId)
      )
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: works }), 500)
      )
    }
    return api.get(`/daily-works/project/${projectId}`)
  },

  save(data) {
    if (USE_MOCK) {
      const newWork = {
        id: mockIdCounter++,
        ...data,
        userId: 1,
        createdAt: new Date().toISOString(),
      }
      MOCK_DAILY_WORKS.unshift(newWork)
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: newWork }), 500)
      )
    }
    return api.post('/daily-works', data)
  },
}
