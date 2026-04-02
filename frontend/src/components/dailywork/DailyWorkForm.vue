<template>
  <div class="daily-work-form card">
    <h3 class="form-title">업무 내용 입력</h3>
    <div class="form-group">
      <textarea
        class="form-textarea work-textarea"
        v-model="content"
        placeholder="오늘 수행한 업무를 입력하세요.&#10;&#10;예시:&#10;- 분류 모델 v2 학습 데이터셋 정제 완료 (총 5,200건)&#10;- 전처리 스크립트 오류 수정 (인코딩 이슈)&#10;- 팀 미팅: 다음 주 일정 논의"
      ></textarea>
    </div>
    <div class="form-actions">
      <button
        class="btn btn-primary"
        :disabled="!content.trim() || saving"
        @click="$emit('save', content)"
      >
        {{ saving ? '저장 중...' : '저장' }}
      </button>
      <button
        class="btn btn-success"
        :disabled="!content.trim() || saving"
        @click="$emit('saveAndDaily', content)"
      >
        저장 + 일보 생성
      </button>
      <button
        class="btn btn-secondary"
        :disabled="!content.trim() || saving"
        @click="$emit('saveAndWeekly', content)"
      >
        저장 + 주보 생성
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  initialContent: { type: String, default: '' },
  saving: { type: Boolean, default: false },
})

defineEmits(['save', 'saveAndDaily', 'saveAndWeekly'])

const content = ref(props.initialContent)

watch(
  () => props.initialContent,
  (val) => { content.value = val }
)

defineExpose({ content, clear: () => { content.value = '' } })
</script>

<style scoped>
.form-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: var(--spacing-md);
}

.work-textarea {
  min-height: 150px;
  line-height: 1.7;
}

.form-actions {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
}
</style>
