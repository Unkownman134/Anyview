<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic :title="t('dashboard.totalUsers')" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic :title="t('dashboard.totalClasses')" :value="stats.totalClasses" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic :title="t('dashboard.totalQuestions')" :value="stats.totalQuestions" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic :title="t('dashboard.totalAssignments')" :value="stats.totalAssignments" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>{{ t('dashboard.welcome') }}</span>
            </div>
          </template>
          <div class="welcome-content">
            <h3>{{ t('dashboard.title') }}</h3>
            <ul v-if="user?.role === 'ADMIN'">
              <li>{{ t('dashboard.admin.1') }}</li>
              <li>{{ t('dashboard.admin.2') }}</li>
              <li>{{ t('dashboard.admin.3') }}</li>
              <li>{{ t('dashboard.admin.4') }}</li>
            </ul>
            <ul v-else-if="user?.role === 'TEACHER'">
              <li>{{ t('dashboard.teacher.1') }}</li>
              <li>{{ t('dashboard.teacher.2') }}</li>
              <li>{{ t('dashboard.teacher.3') }}</li>
              <li>{{ t('dashboard.teacher.4') }}</li>
              <li>{{ t('dashboard.teacher.5') }}</li>
            </ul>
            <ul v-else-if="user?.role === 'STUDENT'">
              <li>{{ t('dashboard.student.1') }}</li>
              <li>{{ t('dashboard.student.2') }}</li>
              <li>{{ t('dashboard.student.3') }}</li>
              <li>{{ t('dashboard.student.4') }}</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { getOverallStatistics } from '@/api/statistics'
import { useI18n } from 'vue-i18n'

const userStore = useUserStore()
const user = computed(() => userStore.user)
const { t } = useI18n()

const stats = reactive({
  totalUsers: 0,
  totalClasses: 0,
  totalQuestions: 0,
  totalAssignments: 0
})

const fetchStats = async () => {
  try {
    const res = await getOverallStatistics()
    console.log('API返回数据:', res)
    if (res.code === 200) {
      console.log('统计数据:', res.data)
      stats.totalUsers = res.data.totalUsers || 0
      stats.totalClasses = res.data.totalClasses || 0
      stats.totalQuestions = res.data.totalQuestions || 0
      stats.totalAssignments = res.data.totalAssignments || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.welcome-content h3 {
  margin-bottom: 15px;
}

.welcome-content ul {
  padding-left: 20px;
}

.welcome-content li {
  margin-bottom: 10px;
  line-height: 1.6;
}
</style>