import { reactive, readonly } from 'vue'

const state = reactive({
  userId: 1,
  userName: '홍길동',
  department: '연구개발팀',
})

export const userStore = readonly(state)
