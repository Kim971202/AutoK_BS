<template>
  <div class="report-history">
    <h3 class="section-title">생성 이력</h3>
    <div v-if="loading" class="loading-wrap">
      <LoadingSpinner message="이력을 불러오는 중..." />
    </div>
    <EmptyState
      v-else-if="!reports.length"
      message="생성된 보고서가 없습니다."
    />
    <div v-else class="history-list">
      <div v-for="report in reports" :key="report.id" class="history-item card">
        <div class="item-header">
          <div class="item-info">
            <span class="type-badge" :class="`type-${report.type.toLowerCase()}`">
              {{ report.type === 'DAILY' ? '일보' : '주보' }}
            </span>
            <span class="item-date">{{ report.targetDate }}</span>
          </div>
          <div class="item-actions">
            <button class="btn btn-ghost btn-sm" @click="$emit('view', report)">보기</button>
            <CopyButton :text="report.content" label="복사" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LoadingSpinner from '../common/LoadingSpinner.vue'
import EmptyState from '../common/EmptyState.vue'
import CopyButton from '../common/CopyButton.vue'

defineProps({
  reports: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['view'])
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
  gap: var(--spacing-sm);
}

.item-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.type-badge {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
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

.item-date {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.item-actions {
  display: flex;
  gap: var(--spacing-sm);
}
</style>
