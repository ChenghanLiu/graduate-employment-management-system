<template>
  <section class="page-card page-section">
    <div class="page-section__header">
      <div>
        <h2>{{ title }}</h2>
        <p v-if="description" class="muted-text">{{ description }}</p>
      </div>
      <div v-if="$slots.extra" class="page-section__extra">
        <slot name="extra" />
      </div>
    </div>
    <div v-loading="loading">
      <slot v-if="!empty" />
      <el-empty v-else :description="emptyDescription" />
    </div>
  </section>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  },
  empty: {
    type: Boolean,
    default: false
  },
  emptyDescription: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style scoped>
.page-section {
  padding: 20px;
}

.page-section__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

h2 {
  margin: 0 0 4px;
  font-size: 20px;
}

p {
  margin: 0;
}

@media (max-width: 768px) {
  .page-section {
    padding: 16px;
  }

  .page-section__header {
    flex-direction: column;
  }
}
</style>
