<template>
  <div class="dashboard">
    <div class="page-header">
      <h1>대시보드</h1>
      <p>{{ userStore.userName }}님, 오늘의 업무를 시작하세요.</p>
    </div>

    <!-- Quick Stats -->
    <div class="stats-grid">
      <div class="stat-card card">
        <div class="stat-icon stat-icon-projects">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" />
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-number">{{ projects.length }}</span>
          <span class="stat-label">진행중 프로젝트</span>
        </div>
      </div>

      <div class="stat-card card">
        <div class="stat-icon stat-icon-works">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-number">{{ todayWorkCount }}</span>
          <span class="stat-label">오늘 입력한 업무</span>
        </div>
      </div>

      <div class="stat-card card">
        <div class="stat-icon stat-icon-reports">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
            <polyline points="14 2 14 8 20 8" />
            <line x1="16" y1="13" x2="8" y2="13" />
            <line x1="16" y1="17" x2="8" y2="17" />
          </svg>
        </div>
        <div class="stat-content">
          <span class="stat-number">{{ reportCount }}</span>
          <span class="stat-label">생성된 보고서</span>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="section">
      <h2 class="section-title">바로가기</h2>
      <div class="quick-actions">
        <router-link to="/projects/create" class="action-card card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>새 프로젝트 등록</span>
        </router-link>
        <router-link to="/daily-work" class="action-card card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
          </svg>
          <span>오늘 업무 입력</span>
        </router-link>
        <router-link to="/reports" class="action-card card">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
            <polyline points="14 2 14 8 20 8" />
          </svg>
          <span>보고서 생성</span>
        </router-link>
      </div>
    </div>

    <!-- Recent Projects -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">진행중 프로젝트</h2>
        <router-link to="/projects" class="btn btn-ghost btn-sm">전체 보기</router-link>
      </div>
      <LoadingSpinner v-if="loading" message="프로젝트를 불러오는 중..." />
      <EmptyState
        v-else-if="!projects.length"
        message="아직 등록된 프로젝트가 없습니다."
        action-label="새 프로젝트 등록하기"
        @action="$router.push('/projects/create')"
      />
      <div v-else class="project-grid">
        <ProjectCard
          v-for="project in projects"
          :key="project.id"
          :project="project"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userStore } from '../stores/userStore'
import projectApi from '../api/projectApi'
import LoadingSpinner from '../components/common/LoadingSpinner.vue'
import EmptyState from '../components/common/EmptyState.vue'
import ProjectCard from '../components/project/ProjectCard.vue'

const projects = ref([])
const loading = ref(false)
const todayWorkCount = ref(1)
const reportCount = ref(2)

onMounted(async () => {
  loading.value = true
  try {
    const res = await projectApi.getAll()
    projects.value = res.data
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1100px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon-projects {
  background-color: var(--color-accent-light);
  color: var(--color-accent);
}

.stat-icon-works {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.stat-icon-reports {
  background-color: var(--color-warning-light);
  color: var(--color-warning);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: var(--spacing-xs);
}

.section {
  margin-bottom: var(--spacing-xl);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.section-header .section-title {
  margin-bottom: 0;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.action-card svg {
  color: var(--color-accent);
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}
</style>
