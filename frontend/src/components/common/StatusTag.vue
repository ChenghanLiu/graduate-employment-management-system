<template>
  <el-tag :type="tagType" :class="tagClass" effect="dark">{{ text }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: '-'
  },
  status: {
    type: [Number, String],
    default: ''
  },
  mode: {
    type: String,
    default: 'default'
  }
})

const tagType = computed(() => {
  if (props.mode === 'review') {
    return {
      0: 'warning',
      1: 'success',
      2: 'danger'
    }[props.status] || 'info'
  }

  if (props.mode === 'job') {
    return {
      0: 'info',
      1: 'success',
      2: 'warning',
      3: 'danger'
    }[props.status] || 'info'
  }

  if (props.mode === 'application') {
    return {
      0: 'info',
      1: 'warning',
      2: 'success',
      3: 'success',
      4: 'danger',
      5: 'info'
    }[props.status] || 'info'
  }

  if (props.mode === 'announcement') {
    return {
      0: 'info',
      1: 'success'
    }[props.status] || 'info'
  }

  return {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'info'
  }[props.status] || 'info'
})

const tagClass = computed(() => {
  return [`status-tag`, `status-tag--${props.mode}`]
})
</script>

<style scoped>
.status-tag {
  font-weight: 600;
  border: none;
}
</style>
