<template>
  <Teleport to="body">
    <div class="toast-container">
      <TransitionGroup name="toast">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="toast-item"
          :class="`toast-${toast.type}`"
        >
          {{ toast.message }}
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
defineProps({
  toasts: { type: Array, required: true },
})
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 72px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.toast-item {
  padding: 12px 20px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  box-shadow: var(--shadow-lg);
  min-width: 240px;
  max-width: 400px;
}

.toast-info {
  background-color: var(--color-accent);
  color: var(--color-text-inverse);
}

.toast-success {
  background-color: var(--color-success);
  color: var(--color-text-inverse);
}

.toast-error {
  background-color: var(--color-danger);
  color: var(--color-text-inverse);
}

.toast-warning {
  background-color: var(--color-warning);
  color: var(--color-text-inverse);
}

.toast-enter-active {
  transition: all 0.3s ease-out;
}
.toast-leave-active {
  transition: all 0.2s ease-in;
}
.toast-enter-from {
  transform: translateX(100%);
  opacity: 0;
}
.toast-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>
