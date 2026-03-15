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
            <ul>
              <li>用户管理：支持管理员、教师、学生三种角色</li>
              <li>班级管理：教师可以创建班级，管理学生</li>
              <li>题库管理：支持创建和管理编程题目</li>
              <li>作业管理：教师可以组题发布作业</li>
              <li>提交记录：学生可以提交作业，教师可以批改</li>
              <li>AI 辅助：智能代码分析和批改功能</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const stats = reactive({
  totalUsers: 0,
  totalClasses: 0,
  totalQuestions: 0,
  totalAssignments: 0
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
