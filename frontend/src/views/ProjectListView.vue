<template>
  <div class="project-list">
    <div class="page-header">
      <div class="header-row">
        <div>
          <h1>프로젝트</h1>
          <p>등록된 프로젝트를 관리합니다.</p>
        </div>
        <router-link to="/projects/create" class="btn btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          새 프로젝트
        </router-link>
      </div>
    </div>

    <LoadingSpinner v-if="loading" message="프로젝트 목록을 불러오는 중..." />

    <EmptyState
      v-else-if="!projects.length"
      message="아직 등록된 프로젝트가 없습니다."
      action-label="새 프로젝트 등록하기"
      @action="$router.push('/projects/create')"
    />

    <div v-else class="projects-grid">
      <ProjectCard
        v-for="project in projects"
        :key="project.id"
        :project="project"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import projectApi from '../api/projectApi'
import ProjectCard from '../components/project/ProjectCard.vue'
import LoadingSpinner from '../components/common/LoadingSpinner.vue'
import EmptyState from '../components/common/EmptyState.vue'

const projects = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await projectApi.getAll()
    projects.value = res.data
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.project-list {
  max-width: 1100px;
}

.header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}
</style>
