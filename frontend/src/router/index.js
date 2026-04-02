import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { title: '대시보드' },
  },
  {
    path: '/projects',
    name: 'ProjectList',
    component: () => import('../views/ProjectListView.vue'),
    meta: { title: '프로젝트' },
  },
  {
    path: '/projects/create',
    name: 'ProjectCreate',
    component: () => import('../views/ProjectCreateView.vue'),
    meta: { title: '프로젝트 등록' },
  },
  {
    path: '/projects/:id',
    name: 'ProjectDetail',
    component: () => import('../views/ProjectDetailView.vue'),
    meta: { title: '프로젝트 상세' },
  },
  {
    path: '/daily-work',
    name: 'DailyWork',
    component: () => import('../views/DailyWorkView.vue'),
    meta: { title: '업무 입력' },
  },
  {
    path: '/reports',
    name: 'Report',
    component: () => import('../views/ReportView.vue'),
    meta: { title: '보고서' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '일보/주보 자동생성기'} | 보고서 생성기`
})

export default router
