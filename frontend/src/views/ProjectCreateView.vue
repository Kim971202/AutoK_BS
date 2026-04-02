<template>
  <div class="project-create">
    <div class="page-header">
      <h1>프로젝트 등록</h1>
      <p>새 프로젝트를 등록하고 스케줄을 설정합니다.</p>
    </div>

    <!-- Step Indicator -->
    <div class="step-indicator">
      <div
        v-for="(s, i) in steps"
        :key="i"
        class="step"
        :class="{ active: currentStep === i, completed: currentStep > i }"
      >
        <div class="step-circle">
          <svg v-if="currentStep > i" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <polyline points="20 6 9 17 4 12" />
          </svg>
          <span v-else>{{ i + 1 }}</span>
        </div>
        <span class="step-label">{{ s }}</span>
        <div v-if="i < steps.length - 1" class="step-line"></div>
      </div>
    </div>

    <!-- STEP 1: Basic Info + Schedule Input -->
    <div v-if="currentStep === 0" class="step-content">
      <div class="form-section card">
        <h2 class="section-title">기본 정보</h2>
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">프로젝트명 <span class="required">*</span></label>
            <input
              class="form-input"
              v-model="form.projectName"
              placeholder="예: AI 기반 문서 분류 시스템"
            />
          </div>
          <div class="form-group">
            <label class="form-label">설명</label>
            <input
              class="form-input"
              v-model="form.description"
              placeholder="프로젝트에 대한 간단한 설명"
            />
          </div>
          <div class="form-group">
            <label class="form-label">시작일 <span class="required">*</span></label>
            <input class="form-input" type="date" v-model="form.startDate" />
          </div>
          <div class="form-group">
            <label class="form-label">종료일 <span class="required">*</span></label>
            <input class="form-input" type="date" v-model="form.endDate" />
          </div>
        </div>
      </div>

      <div class="step-actions">
        <router-link to="/projects" class="btn btn-secondary">취소</router-link>
        <button
          class="btn btn-primary btn-lg"
          :disabled="!isStep1Valid || parsing"
          @click="parseSchedule"
        >
          {{ parsing ? '생성 중...' : 'AI 스케줄 생성' }}
          <svg v-if="!parsing" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="5" y1="12" x2="19" y2="12" />
            <polyline points="12 5 19 12 12 19" />
          </svg>
        </button>
      </div>

      <LoadingSpinner v-if="parsing" message="AI가 프로젝트 일정을 생성하고 있습니다..." overlay />
    </div>

    <!-- STEP 2: Schedule Preview & Edit -->
    <div v-if="currentStep === 1" class="step-content">
      <div class="preview-header">
        <h2 class="section-title">스케줄 분석 결과</h2>
        <p class="section-desc">
          AI가 분석한 주차별 스케줄입니다. 클릭하여 목표를 수정하거나 태스크를 편집할 수 있습니다.
        </p>
      </div>

      <WeekCard
        v-for="week in parsedWeeks"
        :key="week.weekNumber"
        :week="week"
        mode="local"
        :weekId="week.weekId || null"
        @updateGoal="handleUpdateGoal"
        @addTask="handleAddTask"
        @updateTask="handleUpdateTask"
        @deleteTask="handleDeleteTask"
      />

      <div class="step-actions">
        <button class="btn btn-secondary" @click="goBackToStep1">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="19" y1="12" x2="5" y2="12" />
            <polyline points="12 19 5 12 12 5" />
          </svg>
          다시 입력하기
        </button>
        <button class="btn btn-secondary" :disabled="parsing" @click="reparse">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10" />
            <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
          </svg>
          다시 분석
        </button>
        <button
          class="btn btn-primary btn-lg"
          :disabled="confirming"
          @click="confirmSchedule"
        >
          {{ confirming ? '확정 중...' : '이대로 확정' }}
          <svg v-if="!confirming" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="5" y1="12" x2="19" y2="12" />
            <polyline points="12 5 19 12 12 19" />
          </svg>
        </button>
      </div>
    </div>

    <!-- STEP 3: Success -->
    <div v-if="currentStep === 2" class="step-content">
      <div class="success-card card">
        <div class="success-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
        </div>
        <h2 class="success-title">프로젝트가 등록되었습니다!</h2>
        <div class="success-summary">
          <div class="summary-item">
            <span class="summary-label">프로젝트명</span>
            <span class="summary-value">{{ form.projectName }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">기간</span>
            <span class="summary-value">{{ form.startDate }} ~ {{ form.endDate }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">주차</span>
            <span class="summary-value">총 {{ parsedWeeks.length }}주</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">태스크</span>
            <span class="summary-value">총 {{ totalTasks }}건</span>
          </div>
        </div>
        <div class="success-actions">
          <router-link to="/daily-work" class="btn btn-primary btn-lg">
            업무 입력하러 가기
          </router-link>
          <router-link to="/projects" class="btn btn-secondary btn-lg">
            프로젝트 목록
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, inject, reactive } from 'vue'
import WeekCard from '../components/schedule/WeekCard.vue'
import LoadingSpinner from '../components/common/LoadingSpinner.vue'
import scheduleApi from '../api/scheduleApi'
import projectApi from '../api/projectApi'

const toast = inject('toast', null)

const steps = ['프로젝트 정보 입력', '스케줄 확인/수정', '등록 완료']
const currentStep = ref(0)

const form = reactive({
  projectName: '',
  description: '',
  startDate: '',
  endDate: '',
})

const parsing = ref(false)
const confirming = ref(false)
const parsedWeeks = ref([])
const createdProjectId = ref(null)

const isStep1Valid = computed(() => {
  return form.projectName.trim() && form.startDate && form.endDate
})

const totalTasks = computed(() => {
  return parsedWeeks.value.reduce((sum, w) => sum + w.tasks.length, 0)
})

const parseSchedule = async () => {
  parsing.value = true
  try {
    const res = await scheduleApi.parse(form.projectName, form.startDate, form.endDate, form.description)
    parsedWeeks.value = res.data.weeks.map((w) => ({ ...w, tasks: [...w.tasks] }))
    currentStep.value = 1
  } catch {
    if (toast) toast.error('분석에 실패했습니다. 다시 시도해주세요.')
  } finally {
    parsing.value = false
  }
}

const reparse = () => {
  parseSchedule()
}

const goBackToStep1 = () => {
  currentStep.value = 0
}

const handleUpdateGoal = (weekNumber, newGoal) => {
  const week = parsedWeeks.value.find((w) => w.weekNumber === weekNumber)
  if (week) week.goal = newGoal
}

const handleAddTask = (weekNumber, content) => {
  const week = parsedWeeks.value.find((w) => w.weekNumber === weekNumber)
  if (week) {
    week.tasks.push({
      id: 't_' + Date.now(),
      content,
      completed: false,
    })
  }
}

const handleUpdateTask = (weekNumber, taskId, content) => {
  const week = parsedWeeks.value.find((w) => w.weekNumber === weekNumber)
  if (week) {
    const task = week.tasks.find((t) => t.id === taskId)
    if (task) task.content = content
  }
}

const handleDeleteTask = (weekNumber, taskId) => {
  const week = parsedWeeks.value.find((w) => w.weekNumber === weekNumber)
  if (week) {
    week.tasks = week.tasks.filter((t) => t.id !== taskId)
  }
}

const confirmSchedule = async () => {
  confirming.value = true
  try {
    // Create the project first
    const projectRes = await projectApi.create({
      projectName: form.projectName,
      description: form.description,
      startDate: form.startDate,
      endDate: form.endDate,
    })
    createdProjectId.value = projectRes.data.id

    // Then confirm the schedule
    await scheduleApi.confirm(createdProjectId.value, parsedWeeks.value)

    currentStep.value = 2
    if (toast) toast.success('프로젝트가 성공적으로 등록되었습니다!')
  } catch {
    if (toast) toast.error('등록에 실패했습니다. 다시 시도해주세요.')
  } finally {
    confirming.value = false
  }
}
</script>

<style scoped>
.project-create {
  max-width: 900px;
  position: relative;
}

/* Step Indicator */
.step-indicator {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.step {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-tertiary);
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.step.active .step-circle {
  background-color: var(--color-accent);
  color: var(--color-text-inverse);
}

.step.completed .step-circle {
  background-color: var(--color-success);
  color: var(--color-text-inverse);
}

.step-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-tertiary);
  white-space: nowrap;
}

.step.active .step-label {
  color: var(--color-text-primary);
}

.step.completed .step-label {
  color: var(--color-success);
}

.step-line {
  width: 60px;
  height: 2px;
  background-color: var(--color-border);
  margin: 0 var(--spacing-sm);
}

.step.completed .step-line {
  background-color: var(--color-success);
}

/* Form sections */
.form-section {
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.section-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-lg);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.form-grid .form-group:first-child,
.form-grid .form-group:nth-child(2) {
  grid-column: span 1;
}

/* Step actions */
.step-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border);
}

/* Preview header */
.preview-header {
  margin-bottom: var(--spacing-lg);
}

/* Success */
.success-card {
  text-align: center;
  padding: var(--spacing-2xl);
}

.success-icon {
  color: var(--color-success);
  margin-bottom: var(--spacing-lg);
}

.success-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: var(--spacing-xl);
}

.success-summary {
  display: inline-grid;
  grid-template-columns: auto auto;
  gap: var(--spacing-sm) var(--spacing-xl);
  text-align: left;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--color-bg-tertiary);
  border-radius: var(--radius-md);
}

.summary-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.summary-value {
  font-size: 14px;
  color: var(--color-text-primary);
  font-weight: 600;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: var(--spacing-md);
}
</style>
