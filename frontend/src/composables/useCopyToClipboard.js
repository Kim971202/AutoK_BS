import { inject } from 'vue'

export function useCopyToClipboard() {
  const toast = inject('toast', null)

  const copy = async (text) => {
    try {
      await navigator.clipboard.writeText(text)
      if (toast) {
        toast.success('복사되었습니다!')
      }
      return true
    } catch {
      if (toast) {
        toast.error('복사에 실패했습니다.')
      }
      return false
    }
  }

  return { copy }
}
