<template>
  <PageSection title="公告详情" description="查看公告完整内容。">
    <div v-if="announcement">
      <h2 class="detail-title">{{ announcement.title }}</h2>
      <p class="muted-text">
        类型：{{ announcement.type }} ｜ 状态：{{ announcement.statusName }} ｜ 发布时间：{{ formatDateTime(announcement.publishTime || announcement.createTime) }}
      </p>
      <el-divider />
      <div class="detail-content">{{ announcement.content }}</div>
    </div>
  </PageSection>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageSection from '../../components/common/PageSection.vue'
import { getPublishedAnnouncementDetail } from '../../api/announcement'
import { formatDateTime } from '../../utils/format'

const route = useRoute()
const announcement = ref(null)

async function loadData() {
  announcement.value = await getPublishedAnnouncementDetail(route.params.id)
}

onMounted(loadData)
</script>

<style scoped>
.detail-title {
  margin: 0 0 8px;
}

.detail-content {
  white-space: pre-wrap;
  line-height: 1.8;
}
</style>
