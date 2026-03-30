<template>
  <div class="page-grid">
    <div class="stat-grid">
      <div class="stat-card">
        <p class="stat-card__label">企业名称</p>
        <p class="stat-card__value">{{ profile?.enterpriseName || '未建档' }}</p>
        <p class="stat-card__note">{{ profile?.industryName || '请先完善企业档案' }}</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">岗位数</p>
        <p class="stat-card__value">{{ jobs.length }}</p>
        <p class="stat-card__note">招聘中 {{ recruitingCount }} 个，草稿 {{ draftCount }} 个</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">收到投递</p>
        <p class="stat-card__value">{{ applications.length }}</p>
        <p class="stat-card__note">待继续处理 {{ pendingApplications }} 条</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">审核状态</p>
        <p class="stat-card__value">{{ reviewStatus?.reviewStatusName || profile?.reviewStatusName || '-' }}</p>
        <p class="stat-card__note">{{ profile?.reviewRemark || '当前企业档案审核状态正常' }}</p>
      </div>
    </div>



    <PageSection title="企业概览" description="当前企业档案和岗位投递概况。" :loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称">{{ profile?.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ profile?.contactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ profile?.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱">{{ profile?.contactEmail || '-' }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ profile?.industryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="企业规模">{{ profile?.enterpriseScale || '-' }}</el-descriptions-item>
      </el-descriptions>
    </PageSection>



    <PageSection title="我的岗位" description="最近维护的岗位。" :loading="loading" :empty="!jobs.length">
      <el-table :data="jobs.slice(0, 5)" border empty-text="暂无岗位">
        <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <StatusTag :text="row.postStatusName" :status="row.postStatus" mode="job" />
          </template>
        </el-table-column>
        <el-table-column prop="workCity" label="工作城市" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="140" />
      </el-table>
    </PageSection>

    <PageSection title="最新投递" description="最近收到的岗位投递。" :loading="loading" :empty="!applications.length">
      <el-table :data="applications.slice(0, 5)" border empty-text="暂无投递">
        <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="majorName" label="专业" width="160" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <StatusTag :text="row.applicationStatusName" :status="row.applicationStatus" mode="application" />
          </template>
        </el-table-column>
      </el-table>
    </PageSection>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { getCurrentEnterpriseProfile, getCurrentEnterpriseReviewStatus } from '../../api/enterprise'
import { listMyJobPosts } from '../../api/jobPost'
import { listReceivedJobApplications } from '../../api/jobApplication'

const profile = ref(null)
const reviewStatus = ref(null)
const jobs = ref([])
const applications = ref([])
const loading = ref(false)

const recruitingCount = computed(() => jobs.value.filter((item) => item.postStatus === 1).length)
const draftCount = computed(() => jobs.value.filter((item) => item.postStatus === 0).length)
const pendingApplications = computed(() => applications.value.filter((item) => [0, 1].includes(item.applicationStatus)).length)

async function loadData() {
  loading.value = true

  try {
    try {
      profile.value = await getCurrentEnterpriseProfile()
      reviewStatus.value = await getCurrentEnterpriseReviewStatus()
    } catch {}

    jobs.value = await listMyJobPosts().catch(() => [])
    applications.value = await listReceivedJobApplications().catch(() => [])
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

.focus-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.focus-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--app-panel-soft);
  border: 1px solid var(--app-border);
}

.focus-card__label {
  margin: 0 0 6px;
  color: var(--app-primary);
  font-size: 13px;
  font-weight: 700;
}

.focus-card__text {
  margin: 0;
  line-height: 1.7;
}
</style>
