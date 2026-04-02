<template>
  <div class="week-card card" :class="{ expanded: isExpanded }">
    <div class="week-header" @click="toggleExpand">
      <div class="week-info">
        <span class="week-number">{{ week.weekNumber }}주차</span>
        <span class="week-dates">{{ week.startDate }} ~ {{ week.endDate }}</span>
      </div>
      <button class="expand-toggle" :class="{ rotated: isExpanded }" aria-label="일별 스케줄 보기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="6 9 12 15 18 9" />
        </svg>
      </button>
    </div>

    <div class="week-goal">
      <span class="goal-label">목표:</span>
      <span
        v-if="!isEditingGoal"
        class="goal-text"
        @click.stop="startEditGoal"
        title="클릭하여 수정"
      >
        {{ week.goal }}
      </span>
      <input
        v-else
        ref="goalInput"
        v-model="goalText"
        class="form-input goal-input"
        @keyup.enter="saveGoal"
        @keyup.escape="cancelGoal"
        @blur="saveGoal"
        @click.stop
      />
    </div>

    <div class="week-tasks">
      <div class="tasks-header">
        <span class="tasks-title">태스크 ({{ week.tasks.length }})</span>
      </div>
      <TaskItem
        v-for="task in week.tasks"
        :key="task.id"
        :task="task"
        @update="(id, content) => $emit('updateTask', week.weekNumber, id, content)"
        @delete="(id) => $emit('deleteTask', week.weekNumber, id)"
      />
      <div class="add-task">
        <div v-if="!isAddingTask" class="add-task-btn" @click.stop="startAddTask">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          <span>태스크 추가</span>
        </div>
        <div v-else class="add-task-form" @click.stop>
          <input
            ref="newTaskInput"
            v-model="newTaskText"
            class="form-input"
            placeholder="새 태스크 내용을 입력하세요"
            @keyup.enter="addTask"
            @keyup.escape="cancelAddTask"
          />
          <button class="btn btn-primary btn-sm" @click="addTask">추가</button>
          <button class="btn btn-ghost btn-sm" @click="cancelAddTask">취소</button>
        </div>
      </div>
    </div>

    <!-- Daily Breakdown (expandable) -->
    <Transition name="daily-slide">
      <div v-if="isExpanded" class="daily-breakdown">
        <div class="daily-divider"></div>

        <LoadingSpinner v-if="dailyLoading" message="일별 스케줄을 불러오는 중..." />

        <div v-else class="daily-grid">
          <div v-for="day in DAY_ORDER" :key="day.key" class="daily-column">
            <div class="daily-header">{{ day.label }}</div>
            <div class="daily-tasks">
              <template v-if="dailyTasks[day.key] && dailyTasks[day.key].length">
                <div
                  v-for="task in dailyTasks[day.key]"
                  :key="task.id"
                  class="daily-task-pill"
                >
                  {{ task.content }}
                </div>
              </template>
              <span v-else class="daily-empty">&mdash;</span>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, watch } from 'vue'
import TaskItem from './TaskItem.vue'
import LoadingSpinner from '../common/LoadingSpinner.vue'
import scheduleApi from '../../api/scheduleApi'

const DAY_ORDER = [
  { key: 'MONDAY', label: '월' },
  { key: 'TUESDAY', label: '화' },
  { key: 'WEDNESDAY', label: '수' },
  { key: 'THURSDAY', label: '목' },
  { key: 'FRIDAY', label: '금' },
]

const props = defineProps({
  week: { type: Object, required: true },
  /** 'local' = group tasks by dayOfWeek field (wizard step 2), 'api' = fetch from backend */
  mode: { type: String, default: 'local' },
  /** weekId for API fetch mode */
  weekId: { type: [String, Number], default: null },
})

const emit = defineEmits(['updateGoal', 'addTask', 'updateTask', 'deleteTask'])

// Goal editing
const isEditingGoal = ref(false)
const goalText = ref('')
const goalInput = ref(null)

// Task adding
const isAddingTask = ref(false)
const newTaskText = ref('')
const newTaskInput = ref(null)

// Expand / daily breakdown
const isExpanded = ref(false)
const dailyLoading = ref(false)
const fetchedDailyData = ref(null)
const hasFetched = ref(false)

const dailyTasks = computed(() => {
  if (props.mode === 'local') {
    // Group from the week's tasks by dayOfWeek field
    const grouped = {}
    for (const day of DAY_ORDER) {
      grouped[day.key] = props.week.tasks.filter(t => t.dayOfWeek === day.key)
    }
    return grouped
  }
  // API mode — use fetched data
  if (fetchedDailyData.value) {
    return fetchedDailyData.value.days || {}
  }
  return {}
})

const toggleExpand = () => {
  isExpanded.value = !isExpanded.value
  if (isExpanded.value && props.mode === 'api' && !hasFetched.value) {
    fetchDailySchedule()
  }
}

const fetchDailySchedule = async () => {
  if (!props.weekId) return
  dailyLoading.value = true
  try {
    const res = await scheduleApi.getDailySchedule(props.weekId)
    fetchedDailyData.value = res.data
    hasFetched.value = true
  } catch {
    // Error handled silently; empty daily view shown
  } finally {
    dailyLoading.value = false
  }
}

// Goal editing methods
const startEditGoal = async () => {
  goalText.value = props.week.goal
  isEditingGoal.value = true
  await nextTick()
  goalInput.value?.focus()
}

const saveGoal = () => {
  if (!isEditingGoal.value) return
  if (goalText.value.trim()) {
    emit('updateGoal', props.week.weekNumber, goalText.value.trim())
  }
  isEditingGoal.value = false
}

const cancelGoal = () => {
  isEditingGoal.value = false
}

// Task adding methods
const startAddTask = async () => {
  isAddingTask.value = true
  newTaskText.value = ''
  await nextTick()
  newTaskInput.value?.focus()
}

const addTask = () => {
  if (newTaskText.value.trim()) {
    emit('addTask', props.week.weekNumber, newTaskText.value.trim())
    newTaskText.value = ''
    newTaskInput.value?.focus()
  }
}

const cancelAddTask = () => {
  isAddingTask.value = false
  newTaskText.value = ''
}
</script>

<style scoped>
.week-card {
  margin-bottom: var(--spacing-md);
  transition: box-shadow 0.2s ease;
}

.week-card:hover {
  box-shadow: var(--shadow-md);
}

.week-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
  cursor: pointer;
}

.week-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.week-number {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-accent);
}

.week-dates {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.expand-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  color: var(--color-text-tertiary);
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.expand-toggle:hover {
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.expand-toggle.rotated {
  transform: rotate(180deg);
}

.week-goal {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-accent-light);
  border-radius: var(--radius-sm);
}

.goal-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-accent);
  flex-shrink: 0;
}

.goal-text {
  font-size: 14px;
  color: var(--color-text-primary);
  cursor: pointer;
  flex: 1;
}

.goal-text:hover {
  text-decoration: underline dashed;
  text-underline-offset: 3px;
}

.goal-input {
  flex: 1;
  font-size: 14px;
  padding: 4px 8px;
}

.tasks-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
}

.tasks-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.add-task {
  margin-top: var(--spacing-sm);
}

.add-task-btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 13px;
  color: var(--color-accent);
  cursor: pointer;
  padding: var(--spacing-xs) 0;
}

.add-task-btn:hover {
  text-decoration: underline;
}

.add-task-form {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.add-task-form .form-input {
  flex: 1;
  font-size: 14px;
  padding: 6px 10px;
}

/* Daily Breakdown */
.daily-divider {
  height: 1px;
  background-color: var(--color-border);
  margin: var(--spacing-md) 0;
}

.daily-breakdown {
  overflow: hidden;
}

.daily-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: var(--spacing-sm);
}

.daily-column {
  min-height: 60px;
}

.daily-header {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-accent);
  text-align: center;
  padding: var(--spacing-xs) 0;
  margin-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--color-accent);
}

.daily-tasks {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  align-items: center;
}

.daily-task-pill {
  width: 100%;
  font-size: 12px;
  line-height: 1.4;
  color: var(--color-text-primary);
  background-color: var(--color-bg-tertiary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  text-align: center;
  word-break: keep-all;
  overflow-wrap: break-word;
}

.daily-empty {
  font-size: 14px;
  color: var(--color-text-tertiary);
  padding: var(--spacing-sm) 0;
}

/* Slide transition */
.daily-slide-enter-active {
  transition: all 0.3s ease;
  max-height: 500px;
}

.daily-slide-leave-active {
  transition: all 0.25s ease;
  max-height: 500px;
}

.daily-slide-enter-from,
.daily-slide-leave-to {
  opacity: 0;
  max-height: 0;
}
</style>
