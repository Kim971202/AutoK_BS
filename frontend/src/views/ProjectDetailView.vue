<template>
  <div class="project-detail">
    <LoadingSpinner v-if="loading" message="프로젝트를 불러오는 중..." />

    <template v-else-if="project">
      <div class="page-header">
        <div class="header-row">
          <div>
            <h1>{{ project.projectName }}</h1>
            <p>{{ project.description }}</p>
          </div>
          <div class="header-actions">
            <router-link :to="`/daily-work?projectId=${project.id}`" class="btn btn-primary">
              업무 입력
            </router-link>
            <router-link :to="`/reports?projectId=${project.id}`" class="btn btn-secondary">
              보고서 생성
            </router-link>
          </div>
        </div>
      </div>

      <!-- Project Info -->
      <div class="info-grid">
        <div class="info-card card">
          <span class="info-label">기간</span>
          <span class="info-value">{{ project.startDate }} ~ {{ project.endDate }}</span>
        </div>
        <div class="info-card card">
          <span class="info-label">상태</span>
          <span class="info-value">{{ statusLabel }}</span>
        </div>
        <div class="info-card card">
          <span class="info-label">진행률</span>
          <div class="progress-wrap">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: project.progress + '%' }"></div>
            </div>
            <span class="info-value">{{ project.progress }}%</span>
          </div>
        </div>
      </div>

      <!-- Schedule -->
      <div class="section">
        <div class="section-header">
          <h2 class="section-title">주차별 스케줄</h2>
          <p class="section-hint">주차 카드를 클릭하면 일별 상세 스케줄을 확인할 수 있습니다.</p>
        </div>
        <LoadingSpinner v-if="scheduleLoading" message="스케줄을 불러오는 중..." />
        <EmptyState v-else-if="!weeks.length" message="등록된 스케줄이 없습니다." />
        <div v-else class="schedule-list">
          <WeekCard
            v-for="week in weeks"
            :key="week.weekNumber"
            :week="week"
            mode="api"
            :weekId="week.weekId || week.id || null"
          />
        </div>
      </div>
    </template>

    <EmptyState v-else message="프로젝트를 찾을 수 없습니다." action-label="프로젝트 목록" @action="$router.push('/projects')" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import projectApi from '../api/projectApi'
import scheduleApi from '../api/scheduleApi'
import LoadingSpinner from '../components/common/LoadingSpinner.vue'
import EmptyState from '../components/common/EmptyState.vue'
import WeekCard from '../components/schedule/WeekCard.vue'

const route = useRoute()
const project = ref(null)
const weeks = ref([])
const loading = ref(false)
const scheduleLoading = ref(false)

const statusLabel = computed(() => {
  const map = { IN_PROGRESS: '진행중', COMPLETED: '완료', PENDING: '대기' }
  return map[project.value?.status] || ''
})

onMounted(async () => {
  const id = route.params.id
  loading.value = true
  try {
    const res = await projectApi.getById(id)
    project.value = res.data
  } catch {
    // handled
  } finally {
    loading.value = false
  }

  if (project.value) {
    scheduleLoading.value = true
    try {
      const res = await scheduleApi.getByProject(id)
      weeks.value = res.data.weeks || []
    } catch {
      // handled
    } finally {
      scheduleLoading.value = false
    }
  }
})
</script>

<style scoped>
.project-detail {
  max-width: 1000px;
}

.header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.info-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.info-label {
  font-size: 13px;
  color: var(--color-text-tertiary);
  font-weight: 500;
}

.info-value {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: var(--color-bg-tertiary);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--color-accent);
  border-radius: 4px;
}

.section {
  margin-bottom: var(--spacing-xl);
}

.section-header {
  margin-bottom: var(--spacing-md);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
}

.section-hint {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.schedule-list {
  display: flex;
  flex-direction: column;
}
</style>
