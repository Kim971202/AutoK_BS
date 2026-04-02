import { ref } from 'vue'

export function useToast() {
  const toasts = ref([])

  const show = (message, type = 'info') => {
    const id = Date.now() + Math.random()
    toasts.value.push({ id, message, type })
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, 2000)
  }

  const success = (message) => show(message, 'success')
  const error = (message) => show(message, 'error')
  const warning = (message) => show(message, 'warning')
  const info = (message) => show(message, 'info')

  return { toasts, show, success, error, warning, info }
}
