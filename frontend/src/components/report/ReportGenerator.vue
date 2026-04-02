<template>
  <div class="report-generator">
    <div v-if="generating" class="generating-state card">
      <div class="progress-container">
        <div class="progress-bar-track">
          <div class="progress-bar-fill" :style="{ width: progress + '%' }"></div>
        </div>
        <p class="progress-message">{{ currentMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onUnmounted } from 'vue'

const props = defineProps({
  generating: { type: Boolean, default: false },
})

const messages = [
  '업무 내용을 분석하고 있습니다...',
  '스케줄과 비교하고 있습니다...',
  '보고서를 작성하고 있습니다...',
  '문장을 다듬고 있습니다...',
  '최종 검토 중입니다...',
]

const progress = ref(0)
const currentMessage = ref(messages[0])
let messageIndex = 0
let progressInterval = null
let messageInterval = null

const startProgress = () => {
  progress.value = 0
  messageIndex = 0
  currentMessage.value = messages[0]

  progressInterval = setInterval(() => {
    if (progress.value < 90) {
      progress.value += Math.random() * 8 + 2
      if (progress.value > 90) progress.value = 90
    }
  }, 500)

  messageInterval = setInterval(() => {
    messageIndex = (messageIndex + 1) % messages.length
    currentMessage.value = messages[messageIndex]
  }, 3500)
}

const stopProgress = () => {
  clearInterval(progressInterval)
  clearInterval(messageInterval)
  progress.value = 100
}

watch(
  () => props.generating,
  (val) => {
    if (val) startProgress()
    else stopProgress()
  }
)

onUnmounted(() => {
  clearInterval(progressInterval)
  clearInterval(messageInterval)
})
</script>

<style scoped>
.generating-state {
  padding: var(--spacing-xl);
}

.progress-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.progress-bar-track {
  width: 100%;
  max-width: 400px;
  height: 6px;
  background-color: var(--color-bg-tertiary);
  border-radius: 3px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background-color: var(--color-accent);
  border-radius: 3px;
  transition: width 0.5s ease;
}

.progress-message {
  font-size: 14px;
  color: var(--color-text-secondary);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}
</style>
