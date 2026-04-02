<template>
  <AppLayout />
  <Toast :toasts="toasts" />
</template>

<script setup>
import { provide } from 'vue'
import AppLayout from './components/layout/AppLayout.vue'
import Toast from './components/common/Toast.vue'
import { useToast } from './composables/useToast'

const { toasts, show, success, error, warning, info } = useToast()

// Provide toast globally so any component can inject it
provide('toast', { show, success, error, warning, info })
</script>
