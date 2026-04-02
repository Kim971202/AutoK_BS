import api from './index'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

let mockIdCounter = 3

const MOCK_PROJECTS = [
  {
    id: 1,
    projectName: 'AI 기반 문서 분류 시스템',
    description: '딥러닝 모델을 활용한 사내 문서 자동 분류 시스템 개발',
    startDate: '2026-03-01',
    endDate: '2026-06-30',
    status: 'IN_PROGRESS',
    progress: 35,
    createdAt: '2026-03-01T09:00:00',
  },
  {
    id: 2,
    projectName: '데이터 파이프라인 고도화',
    description: '실시간 데이터 처리 파이프라인 성능 개선 및 모니터링 구축',
    startDate: '2026-02-15',
    endDate: '2026-05-15',
    status: 'IN_PROGRESS',
    progress: 60,
    createdAt: '2026-02-15T09:00:00',
  },
]

export default {
  getAll() {
    if (USE_MOCK) {
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: [...MOCK_PROJECTS] }), 500)
      )
    }
    return api.get('/projects')
  },

  getById(id) {
    if (USE_MOCK) {
      const project = MOCK_PROJECTS.find((p) => p.id === Number(id))
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: project || null }), 300)
      )
    }
    return api.get(`/projects/${id}`)
  },

  create(data) {
    if (USE_MOCK) {
      const newProject = {
        id: mockIdCounter++,
        ...data,
        status: 'IN_PROGRESS',
        progress: 0,
        createdAt: new Date().toISOString(),
      }
      MOCK_PROJECTS.push(newProject)
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: newProject }), 500)
      )
    }
    return api.post('/projects', data)
  },

  delete(id) {
    if (USE_MOCK) {
      const idx = MOCK_PROJECTS.findIndex((p) => p.id === Number(id))
      if (idx > -1) MOCK_PROJECTS.splice(idx, 1)
      return new Promise((resolve) =>
        setTimeout(() => resolve({ data: { success: true } }), 300)
      )
    }
    return api.delete(`/projects/${id}`)
  },
}
