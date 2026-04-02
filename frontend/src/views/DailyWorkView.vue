<template>
  <div class="daily-work">
    <div class="page-header">
      <h1>업무 입력</h1>
      <p>오늘 수행한 업무를 입력하고 보고서를 생성합니다.</p>
    </div>

    <!-- Selectors -->
    <div class="selectors card">
      <div class="selector-row">
        <div class="form-group">
          <label class="form-label">프로젝트 선택</label>
          <select class="form-select" v-model="selectedProjectId" @change="onProjectChange">
            <option value="">프로젝트를 선택하세요</option>
            <option v-for="p in projects" :key="p.id" :value="p.id">
              {{ p.projectName }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">작업일</label>
          <input class="form-input" type="date" v-model="workDate" />
        </div>
      </div>
    </div>

    <template v-if="selectedProjectId">
      <!-- Current Week Schedule Reference -->
      <div class="schedule-ref card" v-if="currentWeek">
        <h3 class="ref-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
            <line x1="16" y1="2" x2="16" y2="6" />
            <line x1="8" y1="2" x2="8" y2="6" />
            <line x1="3" y1="10" x2="21" y2="10" />
          </svg>
          {{ currentWeek.weekNumber }}주차 스케줄 (참고용)
        </h3>
        <p class="ref-goal">목표: {{ currentWeek.goal }}</p>
        <ul class="ref-tasks">
          <li v-for="task in currentWeek.tasks" :key="task.id">{{ task.content }}</li>
        </ul>
      </div>

      <!-- Work Input Form -->
      <DailyWorkForm
        ref="workFormRef"
        :saving="saving"
        @save="handleSave"
        @saveAndDaily="handleSaveAndDaily"
        @saveAndWeekly="handleSaveAndWeekly"
      />

      <!-- History -->
      <div class="history-section">
        <DailyWorkHistory :works="works" :loading="historyLoading" />
      </div>
    </template>

    <EmptyState
      v-else-if="!projectsLoading"
      message="프로젝트를 선택하면 업무를 입력할 수 있습니다."
    >
      <template #icon>
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
        </svg>
      </template>
    </EmptyState>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import projectApi from '../api/projectApi'
import dailyWorkApi from '../api/dailyWorkApi'
import scheduleApi from '../api/scheduleApi'
import DailyWorkForm from '../components/dailywork/DailyWorkForm.vue'
import DailyWorkHistory from '../components/dailywork/DailyWorkHistory.vue'
import EmptyState from '../components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const toast = inject('toast', null)

const projects = ref([])
const projectsLoading = ref(false)
const selectedProjectId = ref('')
const workDate = ref(new Date().toISOString().split('T')[0])
const currentWeek = ref(null)
const works = ref([])
const historyLoading = ref(false)
const saving = ref(false)
const workFormRef = ref(null)

onMounted(async () => {
  projectsLoading.value = true
  try {
    const res = await projectApi.getAll()
    projects.value = res.data
    // Check for query param
    if (route.query.projectId) {
      selectedProjectId.value = Number(route.query.projectId)
      onProjectChange()
    }
  } catch {
    // handled
  } finally {
    projectsLoading.value = false
  }
})

const onProjectChange = async () => {
  if (!selectedProjectId.value) return
  // Load schedule
  try {
    const res = await scheduleApi.getByProject(selectedProjectId.value)
    const weeks = res.data.weeks || []
    // Find current week (simplified: use first week for mock)
    currentWeek.value = weeks.length > 0 ? weeks[0] : null
  } catch {
    currentWeek.value = null
  }
  // Load history
  loadHistory()
}

const loadHistory = async () => {
  historyLoading.value = true
  try {
    const res = await dailyWorkApi.getByProject(selectedProjectId.value)
    works.value = res.data
  } catch {
    // handled
  } finally {
    historyLoading.value = false
  }
}

const handleSave = async (content) => {
  saving.value = true
  try {
    await dailyWorkApi.save({
      projectId: Number(selectedProjectId.value),
      workDate: workDate.value,
      content,
    })
    if (toast) toast.success('업무가 저장되었습니다.')
    if (workFormRef.value) workFormRef.value.clear()
    loadHistory()
  } catch {
    if (toast) toast.error('저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

const handleSaveAndDaily = async (content) => {
  await handleSave(content)
  router.push(`/reports?projectId=${selectedProjectId.value}&type=DAILY&date=${workDate.value}`)
}

const handleSaveAndWeekly = async (content) => {
  await handleSave(content)
  router.push(`/reports?projectId=${selectedProjectId.value}&type=WEEKLY&date=${workDate.value}`)
}
</script>

<style scoped>
.daily-work {
  max-width: 900px;
}

.selectors {
  margin-bottom: var(--spacing-lg);
}

.selector-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-md);
}

.form-select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: var(--color-bg-secondary);
  color: var(--color-text-primary);
  cursor: pointer;
}

.form-select:focus {
  border-color: var(--color-accent);
  box-shadow: 0 0 0 3px var(--color-accent-light);
}

.schedule-ref {
  margin-bottom: var(--spacing-lg);
  border-left: 3px solid var(--color-accent);
}

.ref-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-accent);
  margin-bottom: var(--spacing-sm);
}

.ref-goal {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-sm);
}

.ref-tasks {
  list-style: disc;
  padding-left: 20px;
}

.ref-tasks li {
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.7;
}

.history-section {
  margin-top: var(--spacing-xl);
}
</style>
