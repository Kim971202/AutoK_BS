<template>
  <div class="task-item">
    <div class="task-content" v-if="!isEditing">
      <span class="task-text">{{ task.content }}</span>
      <div class="task-actions">
        <button class="btn-icon" title="수정" @click="startEdit">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
          </svg>
        </button>
        <button class="btn-icon btn-icon-danger" title="삭제" @click="$emit('delete', task.id)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6" />
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
          </svg>
        </button>
      </div>
    </div>
    <div class="task-edit" v-else>
      <input
        ref="editInput"
        v-model="editText"
        class="form-input"
        @keyup.enter="saveEdit"
        @keyup.escape="cancelEdit"
        @blur="saveEdit"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  task: { type: Object, required: true },
})

const emit = defineEmits(['update', 'delete'])

const isEditing = ref(false)
const editText = ref('')
const editInput = ref(null)

const startEdit = async () => {
  editText.value = props.task.content
  isEditing.value = true
  await nextTick()
  editInput.value?.focus()
}

const saveEdit = () => {
  if (!isEditing.value) return
  if (editText.value.trim()) {
    emit('update', props.task.id, editText.value.trim())
  }
  isEditing.value = false
}

const cancelEdit = () => {
  isEditing.value = false
}
</script>

<style scoped>
.task-item {
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-bg-tertiary);
}

.task-item:last-child {
  border-bottom: none;
}

.task-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-sm);
}

.task-text {
  font-size: 14px;
  color: var(--color-text-primary);
  flex: 1;
}

.task-actions {
  display: flex;
  gap: var(--spacing-xs);
  opacity: 0;
  transition: opacity 0.15s ease;
}

.task-item:hover .task-actions {
  opacity: 1;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  color: var(--color-text-tertiary);
  transition: all 0.15s ease;
}

.btn-icon:hover {
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.btn-icon-danger:hover {
  background-color: var(--color-danger-light);
  color: var(--color-danger);
}

.task-edit .form-input {
  width: 100%;
  font-size: 14px;
  padding: 6px 10px;
}
</style>
