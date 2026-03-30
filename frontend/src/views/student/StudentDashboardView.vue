<template>
  <div class="page-grid">
    <div class="stat-grid">
      <div class="stat-card">
        <p class="stat-card__label">当前学号</p>
        <p class="stat-card__value">{{ profile?.studentNo || '未建档' }}</p>
        <p class="stat-card__note">{{ profile?.collegeName || '请先完善学生档案' }}</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">简历完成度</p>
        <p class="stat-card__value">{{ resume?.completionRate ?? 0 }}%</p>
        <p class="stat-card__note">{{ resume?.resumeName || '当前还没有默认简历' }}</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">我的投递</p>
        <p class="stat-card__value">{{ applications.length }}</p>
        <p class="stat-card__note">最近状态：{{ applications[0]?.applicationStatusName || '暂无投递' }}</p>
      </div>
      <div class="stat-card">
        <p class="stat-card__label">就业状态</p>
        <p class="stat-card__value">{{ profile?.employmentStatusName || employmentRecord?.employmentStatusName || '-' }}</p>
        <p class="stat-card__note">{{ employmentRecord?.companyName || '当前未填写就业去向' }}</p>
      </div>
    </div>



    <PageSection title="当前状态" description="展示学生基础档案、求职简历和就业登记的关键摘要。" :loading="loading">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学院 / 专业">
          {{ profile ? `${profile.collegeName} / ${profile.majorName}` : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="就业状态">
          {{ profile?.employmentStatusName || employmentRecord?.employmentStatusName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="简历名称">
          {{ resume?.resumeName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="求职意向">
          {{ resume?.jobIntention || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="就业单位">
          {{ employmentRecord?.companyName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="岗位名称">
          {{ employmentRecord?.jobTitle || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </PageSection>

    <PageSection title="个人摘要" description="">
      <div class="brief-grid">
        <div class="brief-card">
          <p class="brief-card__label">学生画像</p>
          <p class="brief-card__text">
            {{ profile ? `${profile.collegeName} ${profile.majorName}，毕业年份 ${profile.graduationYear}` : '当前尚未加载学生档案。' }}
          </p>
        </div>
        <div class="brief-card">
          <p class="brief-card__label">求职方向</p>
          <p class="brief-card__text">
            {{ resume ? `${resume.jobIntention || '-'}，期望城市 ${resume.expectedCity || '-'}` : '当前尚未加载默认简历。' }}
          </p>
        </div>
        <div class="brief-card">
          <p class="brief-card__label">最近提醒</p>
          <p class="brief-card__text">
            {{ announcements[0]?.title || '暂无最新公告，可前往公告中心查看。' }}
          </p>
        </div>
      </div>
    </PageSection>

    <PageSection title="最新投递" description="最近岗位投递记录。" :loading="loading" :empty="!applications.length">
      <el-table :data="applications.slice(0, 5)" border empty-text="暂无投递记录">
        <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
        <el-table-column prop="enterpriseName" label="企业" min-width="180" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <StatusTag :text="row.applicationStatusName" :status="row.applicationStatus" mode="application" />
          </template>
        </el-table-column>
        <el-table-column label="投递时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.applyTime) }}</template>
        </el-table-column>
      </el-table>
    </PageSection>

    <PageSection title="最新公告" description="已发布公告列表。" :loading="loading" :empty="!announcements.length">
      <el-table :data="announcements.slice(0, 5)" border empty-text="暂无公告">
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="type" label="类型" width="160" />
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">{{ formatDateTime(row.publishTime || row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </PageSection>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { getCurrentStudentProfile } from '../../api/student'
import { getCurrentResume } from '../../api/resume'
import { listMyJobApplications } from '../../api/jobApplication'
import { getCurrentEmploymentRecord } from '../../api/employmentRecord'
import { listPublishedAnnouncements } from '../../api/announcement'
import { formatDateTime } from '../../utils/format'

const profile = ref(null)
const resume = ref(null)
const employmentRecord = ref(null)
const applications = ref([])
const announcements = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true

  try {
    try {
      profile.value = await getCurrentStudentProfile()
    } catch {}

    try {
      resume.value = await getCurrentResume()
    } catch {}

    try {
      employmentRecord.value = await getCurrentEmploymentRecord()
    } catch {}

    applications.value = await listMyJobApplications()
    announcements.value = await listPublishedAnnouncements()
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

.brief-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.brief-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--app-panel-soft);
  border: 1px solid var(--app-border);
}

.brief-card__label {
  margin: 0 0 6px;
  color: var(--app-primary);
  font-size: 13px;
  font-weight: 700;
}

.brief-card__text {
  margin: 0;
  line-height: 1.7;
}
</style>
