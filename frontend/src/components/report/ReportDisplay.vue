<template>
  <div class="report-display card">
    <div class="report-header">
      <div class="report-info">
        <span class="report-type-badge" :class="`type-${report.type.toLowerCase()}`">
          {{ report.type === 'DAILY' ? '일보' : '주보' }}
        </span>
        <span class="report-date">{{ report.targetDate }}</span>
      </div>
      <div class="report-actions">
        <CopyButton :text="report.content" label="전체 복사" />
        <button class="btn btn-secondary btn-sm" @click="$emit('regenerate')">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10" />
            <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10" />
          </svg>
          재생성
        </button>
      </div>
    </div>
    <pre class="report-content">{{ report.content }}</pre>
  </div>
</template>

<script setup>
import CopyButton from '../common/CopyButton.vue'

defineProps({
  report: { type: Object, required: true },
})

defineEmits(['regenerate'])
</script>

<style scoped>
.report-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
}

.report-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.report-type-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
}

.type-daily {
  background-color: var(--color-accent-light);
  color: var(--color-accent);
}

.type-weekly {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.report-date {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.report-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.report-content {
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--color-text-primary);
  background: var(--color-bg-tertiary);
  padding: var(--spacing-lg);
  border-radius: var(--radius-md);
  font-family: var(--font-mono);
}
</style>
