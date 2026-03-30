<template>
  <div class="page-grid">
    <div class="stat-grid">
      <div class="stat-card">
        <p class="stat-card__label">学生总数</p>
        <p class="stat-card__value">{{ adminOverview?.totalStudents ?? statisticsOverview?.totalStudents ?? 0 }}</p>
        <p class="stat-card__note">已就业 {{ statisticsOverview?.employedStudents ?? 0 }} 人</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">企业总数</p>
        <p class="stat-card__value">{{ adminOverview?.totalEnterprises ?? 0 }}</p>
        <p class="stat-card__note">当前可展示企业档案与审核状态</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">岗位总数</p>
        <p class="stat-card__value">{{ adminOverview?.totalJobs ?? jobStats?.totalJobs ?? 0 }}</p>
        <p class="stat-card__note">投递总数 {{ adminOverview?.totalApplications ?? jobStats?.totalApplications ?? 0 }}</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">就业率</p>
        <p class="stat-card__value">{{ statisticsOverview?.employmentRate ?? 0 }}</p>
        <p class="stat-card__note">待审核记录 {{ adminOverview?.pendingEmploymentReviews ?? 0 }} 条</p>
      </div>
    </div>



    <PageSection title="统计概览" description="管理员概览与统计接口组合展示。" :loading="loading">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="已就业学生">{{ statisticsOverview?.employedStudents ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="未就业学生">{{ statisticsOverview?.unemployedStudents ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="待审核就业记录">{{ adminOverview?.pendingEmploymentReviews ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="投递总数">{{ adminOverview?.totalApplications ?? jobStats?.totalApplications ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="录用数">{{ jobStats?.hiredCount ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="企业总数">{{ adminOverview?.totalEnterprises ?? '-' }}</el-descriptions-item>
      </el-descriptions>
    </PageSection>

    <PageSection title="就业状态统计" description="统计信息实时展示。" :loading="loading" :empty="!employmentStatusStats.length">
      <el-table :data="employmentStatusStats" border empty-text="暂无统计数据">
        <el-table-column prop="employmentStatusName" label="就业状态" min-width="180" />
        <el-table-column prop="studentCount" label="学生数量" width="160" />
      </el-table>
    </PageSection>

    <PageSection title="最新公告" description="" :loading="loading" :empty="!announcements.length">
      <el-table :data="announcements.slice(0, 5)" border empty-text="暂无公告数据">
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="type" label="类型" width="140" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <StatusTag :text="row.statusName" :status="row.status" mode="announcement" />
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">{{ row.publishTime || row.createTime || '-' }}</template>
        </el-table-column>
      </el-table>
    </PageSection>

    <PageSection title="待处理审核" description="当前审核列表可直接体现系统业务闭环。" :loading="loading" :empty="!reviewRecords.length">
      <el-table :data="reviewRecords.slice(0, 5)" border empty-text="暂无就业审核数据">
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="companyName" label="就业单位" min-width="180" />
        <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <StatusTag :text="row.reviewStatusName" :status="row.reviewStatus" mode="review" />
          </template>
        </el-table-column>
      </el-table>
    </PageSection>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { listAdminAnnouncements } from '../../api/admin'
import { listEmploymentReviewRecords } from '../../api/employmentRecord'
import {
  getAdminDashboardOverview,
  getEmploymentStatusStatistics,
  getJobStatistics,
  getStatisticsOverview
} from '../../api/statistics'
import { getCurrentIdentity } from '../../utils/identity'

const adminOverview = ref(null)
const statisticsOverview = ref(null)
const employmentStatusStats = ref([])
const jobStats = ref(null)
const announcements = ref([])
const reviewRecords = ref([])
const loading = ref(false)

const identity = getCurrentIdentity()
const role = identity?.role

function isAdmin() {
  return role === 'ADMIN'
}

function isTeacher() {
  return role === 'EMPLOYMENT_TEACHER' || role === 'COUNSELOR'
}

async function loadData() {
  loading.value = true

  try {
    // 统计：老师和管理员都可以
    if (isAdmin() || isTeacher()) {
      statisticsOverview.value = await getStatisticsOverview().catch(() => null)
      employmentStatusStats.value = await getEmploymentStatusStatistics().catch(() => [])
      jobStats.value = await getJobStatistics().catch(() => null)
      reviewRecords.value = await listEmploymentReviewRecords().catch(() => [])
    }

    // 只有管理员才请求 admin-only 数据
    if (isAdmin()) {
      adminOverview.value = await getAdminDashboardOverview().catch(() => null)
      announcements.value = await listAdminAnnouncements().catch(() => [])
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.stat-card__note {
  margin: 8px 0 0;
  color: var(--app-text-muted);
  font-size: 13px;
}
</style>
