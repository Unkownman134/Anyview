<template>
  <div class="data-analysis">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="系统概览" name="system">
        <SystemOverview v-if="activeTab === 'system'" />
      </el-tab-pane>
      <el-tab-pane label="用户分析" name="user">
        <UserAnalysis v-if="activeTab === 'user'" />
      </el-tab-pane>
      <el-tab-pane label="题目分析" name="question">
        <QuestionAnalysis v-if="activeTab === 'question'" />
      </el-tab-pane>
      <el-tab-pane label="班级分析" name="class">
        <ClassAnalysis v-if="activeTab === 'class'" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SystemOverview from '../components/analysis/SystemOverview.vue'
import UserAnalysis from '../components/analysis/UserAnalysis.vue'
import QuestionAnalysis from '../components/analysis/QuestionAnalysis.vue'
import ClassAnalysis from '../components/analysis/ClassAnalysis.vue'

const activeTab = ref('system')
</script>

<style scoped>
.data-analysis {
  padding: 20px;
}
</style>
