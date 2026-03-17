<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总用户数" :value="stats.totalUsers" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总班级数" :value="stats.totalClasses" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总题目数" :value="stats.totalQuestions" />
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总作业数" :value="stats.totalAssignments" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>欢迎使用 AnyView 编程实训平台</span>
            </div>
          </template>
          <div class="welcome-content">
            <h3>平台功能介绍</h3>
            <ul v-if="user?.role === 'ADMIN'">
              <li>用户管理：管理所有用户账号和权限</li>
              <li>学校管理：管理学校信息</li>
              <li>API配置：配置AI评分接口</li>
              <li>查看统计：查看平台整体数据</li>
            </ul>
            <ul v-else-if="user?.role === 'TEACHER'">
              <li>班级管理：创建班级，管理学生</li>
              <li>题库管理：创建和管理编程题目</li>
              <li>作业管理：组题发布作业</li>
              <li>批改作业：查看学生提交并评分</li>
              <li>AI辅助：智能代码分析和批改</li>
            </ul>
            <ul v-else-if="user?.role === 'STUDENT'">
              <li>查看作业：查看老师发布的作业</li>
              <li>提交作业：在线编写并提交代码</li>
              <li>查看成绩：查看作业评分和反馈</li>
              <li>AI反馈：获得智能代码建议</li>
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

const userStore = useUserStore()
const user = computed(() => userStore.user)

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
