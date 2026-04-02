<template>
  <div class="report-view">
    <div class="page-header">
      <h1>보고서</h1>
      <p>일보/주보를 자동으로 생성합니다.</p>
    </div>

    <!-- Controls -->
    <div class="controls card">
      <div class="controls-row">
        <div class="form-group">
          <label class="form-label">프로젝트</label>
          <select class="form-select" v-model="selectedProjectId" @change="loadHistory">
            <option value="">프로젝트를 선택하세요</option>
            <option v-for="p in projects" :key="p.id" :value="p.id">
              {{ p.projectName }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">보고서 유형</label>
          <div class="type-toggle">
            <button
              class="toggle-btn"
              :class="{ active: reportType === 'DAILY' }"
              @click="reportType = 'DAILY'"
            >
              일보
            </button>
            <button
              class="toggle-btn"
              :class="{ active: reportType === 'WEEKLY' }"
              @click="reportType = 'WEEKLY'"
            >
              주보
            </button>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">기준일</label>
          <input class="form-input" type="date" v-model="targetDate" />
        </div>
        <div class="form-group generate-group">
          <label class="form-label">&nbsp;</label>
          <button
            class="btn btn-primary"
            :disabled="!selectedProjectId || generating"
            @click="generateReport"
          >
            {{ generating ? '생성 중...' : '보고서 생성' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Generator progress -->
    <ReportGenerator :generating="generating" />

    <!-- Generated report -->
    <div v-if="currentReport && !generating" class="report-result">
      <ReportDisplay
        :report="currentReport"
        @regenerate="generateReport"
      />
    </div>

    <!-- No report yet -->
    <EmptyState
      v-if="!currentReport && !generating && selectedProjectId"
      message="프로젝트와 유형을 선택한 후 보고서를 생성하세요."
    >
      <template #icon>
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <polyline points="14 2 14 8 20 8" />
          <line x1="16" y1="13" x2="8" y2="13" />
          <line x1="16" y1="17" x2="8" y2="17" />
        </svg>
      </template>
    </EmptyState>

    <!-- Report History -->
    <div v-if="selectedProjectId" class="history-section">
      <ReportHistory
        :reports="reportHistory"
        :loading="historyLoading"
        @view="viewReport"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import projectApi from '../api/projectApi'
import reportApi from '../api/reportApi'
import ReportGenerator from '../components/report/ReportGenerator.vue'
import ReportDisplay from '../components/report/ReportDisplay.vue'
import ReportHistory from '../components/report/ReportHistory.vue'
import EmptyState from '../components/common/EmptyState.vue'

const route = useRoute()
const toast = inject('toast', null)

const projects = ref([])
const selectedProjectId = ref('')
const reportType = ref('DAILY')
const targetDate = ref(new Date().toISOString().split('T')[0])
const generating = ref(false)
const currentReport = ref(null)
const reportHistory = ref([])
const historyLoading = ref(false)

onMounted(async () => {
  try {
    const res = await projectApi.getAll()
    projects.value = res.data
  } catch {
    // handled
  }

  // Check for query params
  if (route.query.projectId) {
    selectedProjectId.value = Number(route.query.projectId)
  }
  if (route.query.type) {
    reportType.value = route.query.type
  }
  if (route.query.date) {
    targetDate.value = route.query.date
  }

  if (selectedProjectId.value) {
    loadHistory()
    // Auto-generate if coming from daily work
    if (route.query.type) {
      generateReport()
    }
  }
})

const loadHistory = async () => {
  if (!selectedProjectId.value) return
  historyLoading.value = true
  try {
    const res = await reportApi.getHistory(selectedProjectId.value)
    reportHistory.value = res.data
  } catch {
    // handled
  } finally {
    historyLoading.value = false
  }
}

const generateReport = async () => {
  generating.value = true
  currentReport.value = null
  try {
    const res = await reportApi.generate({
      projectId: Number(selectedProjectId.value),
      type: reportType.value,
      targetDate: targetDate.value,
    })
    currentReport.value = res.data
    if (toast) toast.success('보고서가 생성되었습니다!')
    loadHistory()
  } catch {
    if (toast) toast.error('보고서 생성에 실패했습니다. 다시 시도해주세요.')
  } finally {
    generating.value = false
  }
}

const viewReport = (report) => {
  currentReport.value = report
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.report-view {
  max-width: 1000px;
}

.controls {
  margin-bottom: var(--spacing-lg);
}

.controls-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: var(--spacing-md);
  align-items: end;
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

.type-toggle {
  display: flex;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.toggle-btn {
  flex: 1;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 500;
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  transition: all 0.15s ease;
  border-right: 1px solid var(--color-border);
}

.toggle-btn:last-child {
  border-right: none;
}

.toggle-btn.active {
  background: var(--color-accent);
  color: var(--color-text-inverse);
}

.toggle-btn:hover:not(.active) {
  background: var(--color-bg-tertiary);
}

.generate-group {
  min-width: 140px;
}

.report-result {
  margin-bottom: var(--spacing-xl);
}

.history-section {
  margin-top: var(--spacing-xl);
}
</style>
