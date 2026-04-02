<template>
  <router-link :to="`/projects/${project.id}`" class="project-card card">
    <div class="card-header">
      <h3 class="project-name">{{ project.projectName }}</h3>
      <span class="status-badge" :class="`status-${project.status.toLowerCase()}`">
        {{ statusLabel }}
      </span>
    </div>
    <p class="project-desc">{{ project.description }}</p>
    <div class="project-meta">
      <span class="meta-item">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" />
          <line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
        {{ project.startDate }} ~ {{ project.endDate }}
      </span>
    </div>
    <div class="progress-bar">
      <div class="progress-fill" :style="{ width: project.progress + '%' }"></div>
    </div>
    <span class="progress-label">진행률 {{ project.progress }}%</span>
  </router-link>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  project: { type: Object, required: true },
})

const statusLabel = computed(() => {
  const map = {
    IN_PROGRESS: '진행중',
    COMPLETED: '완료',
    PENDING: '대기',
  }
  return map[props.project.status] || props.project.status
})
</script>

<style scoped>
.project-card {
  display: block;
  cursor: pointer;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
}

.project-name {
  font-size: 16px;
  font-weight: 600;
}

.status-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
}

.status-in_progress {
  background-color: var(--color-accent-light);
  color: var(--color-accent);
}

.status-completed {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.status-pending {
  background-color: var(--color-warning-light);
  color: var(--color-warning);
}

.project-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-md);
  line-height: 1.5;
}

.project-meta {
  margin-bottom: var(--spacing-md);
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.progress-bar {
  height: 6px;
  background-color: var(--color-bg-tertiary);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: var(--spacing-xs);
}

.progress-fill {
  height: 100%;
  background-color: var(--color-accent);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-label {
  font-size: 12px;
  color: var(--color-text-tertiary);
}
</style>
