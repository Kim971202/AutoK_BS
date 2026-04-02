<template>
  <div class="work-history">
    <h3 class="section-title">이전 업무 기록</h3>
    <div v-if="loading" class="loading-wrap">
      <LoadingSpinner message="기록을 불러오는 중..." />
    </div>
    <EmptyState
      v-else-if="!works.length"
      message="입력된 업무 기록이 없습니다."
    />
    <div v-else class="history-list">
      <div v-for="work in works" :key="work.id" class="history-item card">
        <div class="history-header">
          <span class="history-date">{{ work.workDate }}</span>
          <CopyButton :text="work.content" label="복사" />
        </div>
        <pre class="history-content">{{ work.content }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import LoadingSpinner from '../common/LoadingSpinner.vue'
import EmptyState from '../common/EmptyState.vue'
import CopyButton from '../common/CopyButton.vue'

defineProps({
  works: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})
</script>

<style scoped>
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.loading-wrap {
  padding: var(--spacing-lg);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
}

.history-date {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-accent);
}

.history-content {
  font-size: 14px;
  color: var(--color-text-primary);
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.7;
  font-family: var(--font-sans);
  background: var(--color-bg-tertiary);
  padding: var(--spacing-md);
  border-radius: var(--radius-sm);
}
</style>
