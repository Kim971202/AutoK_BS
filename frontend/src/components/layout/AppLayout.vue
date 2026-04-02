<template>
  <div class="app-layout">
    <AppHeader />
    <AppSidebar />
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <Transition name="fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
</script>

<style scoped>
.app-main {
  margin-top: var(--header-height);
  margin-left: var(--sidebar-width);
  padding: var(--spacing-xl);
  min-height: calc(100vh - var(--header-height));
}
</style>
