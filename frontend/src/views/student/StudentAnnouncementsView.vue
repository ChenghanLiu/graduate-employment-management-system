<template>
  <PageSection title="公告中心" description="查看已发布公告并进入详情页，便于演示通知、政策和活动信息。">
    <el-table :data="announcements" border empty-text="当前暂无已发布公告">
      <el-table-column prop="title" label="标题" min-width="240" />
      <el-table-column prop="type" label="公告类型" width="140" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.statusName" :status="row.status" mode="announcement" />
        </template>
      </el-table-column>
      <el-table-column label="内容摘要" min-width="280">
        <template #default="{ row }">{{ summarize(row.content) }}</template>
      </el-table-column>
      <el-table-column label="发布时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.publishTime || row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/student/announcements/${row.id}`)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageSection>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { listPublishedAnnouncements } from '../../api/announcement'
import { formatDateTime } from '../../utils/format'

const announcements = ref([])

async function loadData() {
  announcements.value = await listPublishedAnnouncements()
}

function summarize(text) {
  if (!text) {
    return '-'
  }

  return text.length > 42 ? `${text.slice(0, 42)}...` : text
}

onMounted(loadData)
</script>
