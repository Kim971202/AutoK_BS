<template>
  <div class="schedule-input">
    <div class="form-group">
      <label class="form-label">
        전체 일정 입력 <span class="required">*</span>
      </label>
      <textarea
        class="form-textarea schedule-textarea"
        v-model="text"
        placeholder="프로젝트 스케줄을 자유 형식으로 입력하세요.&#10;&#10;예시:&#10;1주차: 요구사항 분석, 환경 구성&#10;2주차: 데이터 수집 및 전처리&#10;3주차: 모델 개발 및 학습&#10;4주차: 테스트 및 배포"
        @input="$emit('update:modelValue', text)"
      ></textarea>
      <div class="form-hint">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <line x1="12" y1="16" x2="12" y2="12" />
          <line x1="12" y1="8" x2="12.01" y2="8" />
        </svg>
        TIP: "1주차: 할 일" 형식으로 작성하면 더 정확하게 분석됩니다.
        자유 형식으로 작성해도 AI가 자동으로 주차별로 분류합니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
})

defineEmits(['update:modelValue'])

const text = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => {
    text.value = val
  }
)
</script>

<style scoped>
.schedule-textarea {
  min-height: 200px;
  font-family: var(--font-sans);
  line-height: 1.7;
}

.form-hint {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-xs);
  font-size: 13px;
  color: var(--color-text-tertiary);
  line-height: 1.5;
}

.form-hint svg {
  flex-shrink: 0;
  margin-top: 2px;
}
</style>
