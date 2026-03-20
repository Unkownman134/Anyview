<template>
  <div class="system-overview">
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6" v-for="stat in overviewStats" :key="stat.title">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatNumber(stat.value) }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户活跃度趋势</span>
              <el-radio-group v-model="timeRange" size="small" @change="loadData">
                <el-radio-button label="7">7天</el-radio-button>
                <el-radio-button label="30">30天</el-radio-button>
                <el-radio-button label="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="userTrendChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>提交量趋势</span>
            </div>
          </template>
          <div ref="submissionTrendChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>提交时段分布</span>
            </div>
          </template>
          <div ref="hourDistributionChart" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>提交星期分布</span>
            </div>
          </template>
          <div ref="dayOfWeekChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门题目 Top 10</span>
            </div>
          </template>
          <el-table :data="hotQuestions" style="width: 100%" max-height="400">
            <el-table-column type="index" width="50" />
            <el-table-column prop="title" label="题目" show-overflow-tooltip />
            <el-table-column prop="difficulty" label="难度" width="80">
              <template #default="{ row }">
                <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                  {{ row.difficulty }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submissionCount" label="提交数" width="100" sortable />
            <el-table-column prop="acceptanceRate" label="通过率" width="100">
              <template #default="{ row }">
                {{ row.acceptanceRate?.toFixed(1) }}%
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>错误类型分布</span>
            </div>
          </template>
          <div ref="errorTypeChart" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最活跃用户 Top 10</span>
            </div>
          </template>
          <el-table :data="activeUsers" style="width: 100%">
            <el-table-column type="index" width="50" />
            <el-table-column prop="realName" label="姓名" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="submissionCount" label="提交数" sortable />
            <el-table-column prop="solvedCount" label="解题数" sortable />
            <el-table-column prop="averageScore" label="平均分">
              <template #default="{ row }">
                {{ row.averageScore?.toFixed(1) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { User, Document, Check, Star } from '@element-plus/icons-vue'
import { getSystemAnalysis } from '../../api/analysis'
import { ElMessage } from 'element-plus'

const timeRange = ref('30')
const analysisData = ref<any>(null)
const overviewStats = ref<any[]>([])
const hotQuestions = ref<any[]>([])
const activeUsers = ref<any[]>([])

let userTrendChart: echarts.ECharts | null = null
let submissionTrendChart: echarts.ECharts | null = null
let hourDistributionChart: echarts.ECharts | null = null
let dayOfWeekChart: echarts.ECharts | null = null
let errorTypeChart: echarts.ECharts | null = null

const userTrendChartRef = ref<HTMLElement>()
const submissionTrendChartRef = ref<HTMLElement>()
const hourDistributionChartRef = ref<HTMLElement>()
const dayOfWeekChartRef = ref<HTMLElement>()
const errorTypeChartRef = ref<HTMLElement>()

const loadData = async () => {
  try {
    const res = await getSystemAnalysis(parseInt(timeRange.value))
    if (res.code === 200) {
      analysisData.value = res.data
      updateOverviewStats()
      updateHotQuestions()
      updateActiveUsers()
      nextTick(() => {
        initCharts()
      })
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const updateOverviewStats = () => {
  const overview = analysisData.value?.overview
  if (!overview) return

  overviewStats.value = [
    { title: '总用户数', value: overview.totalUsers, icon: User, color: '#409EFF' },
    { title: '总提交数', value: overview.totalSubmissions, icon: Document, color: '#67C23A' },
    { title: '平均分数', value: overview.averageScore?.toFixed(1), icon: Star, color: '#E6A23C' },
    { title: '通过率', value: overview.acceptanceRate?.toFixed(1) + '%', icon: Check, color: '#F56C6C' }
  ]
}

const updateHotQuestions = () => {
  hotQuestions.value = analysisData.value?.hotQuestions || []
}

const updateActiveUsers = () => {
  activeUsers.value = analysisData.value?.mostActiveUsers || []
}

const initCharts = () => {
  initUserTrendChart()
  initSubmissionTrendChart()
  initHourDistributionChart()
  initDayOfWeekChart()
  initErrorTypeChart()
}

const initUserTrendChart = () => {
  if (!userTrendChartRef.value) return
  userTrendChart = echarts.init(userTrendChartRef.value)

  const trend = analysisData.value?.userTrend || []
  const option: any = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['活跃用户', '新用户'] },
    xAxis: { type: 'category', data: trend.map((t: any) => t.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '活跃用户',
        type: 'line',
        data: trend.map((t: any) => t.value),
        smooth: true,
        areaStyle: { opacity: 0.3 }
      },
      {
        name: '新用户',
        type: 'line',
        data: trend.map((t: any) => t.secondaryValue),
        smooth: true
      }
    ]
  }
  userTrendChart.setOption(option)
}

const initSubmissionTrendChart = () => {
  if (!submissionTrendChartRef.value) return
  submissionTrendChart = echarts.init(submissionTrendChartRef.value)

  const trend = analysisData.value?.submissionTrend || []
  const option: any = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['提交数', '通过数'] },
    xAxis: { type: 'category', data: trend.map((t: any) => t.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '提交数',
        type: 'bar',
        data: trend.map((t: any) => t.value)
      },
      {
        name: '通过数',
        type: 'bar',
        data: trend.map((t: any) => t.secondaryValue)
      }
    ]
  }
  submissionTrendChart.setOption(option)
}

const initHourDistributionChart = () => {
  if (!hourDistributionChartRef.value) return
  hourDistributionChart = echarts.init(hourDistributionChartRef.value)

  const data = analysisData.value?.submissionsByHour || {}
  const option: any = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: Object.values(data),
      itemStyle: { color: '#409EFF' }
    }]
  }
  hourDistributionChart.setOption(option)
}

const initDayOfWeekChart = () => {
  if (!dayOfWeekChartRef.value) return
  dayOfWeekChart = echarts.init(dayOfWeekChartRef.value)

  const data = analysisData.value?.submissionsByDayOfWeek || {}
  const option: any = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: Object.values(data),
      itemStyle: { color: '#67C23A' }
    }]
  }
  dayOfWeekChart.setOption(option)
}

const initErrorTypeChart = () => {
  if (!errorTypeChartRef.value) return
  errorTypeChart = echarts.init(errorTypeChartRef.value)

  const data = analysisData.value?.errorTypeStats || []
  const option: any = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data.map((d: any) => ({ name: d.errorType, value: d.count })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  errorTypeChart.setOption(option)
}

const getDifficultyType = (difficulty: string) => {
  const map: Record<string, string> = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return map[difficulty] || 'info'
}

const formatNumber = (num: number | string) => {
  if (typeof num === 'string') return num
  return num?.toLocaleString() || '0'
}

const handleResize = () => {
  userTrendChart?.resize()
  submissionTrendChart?.resize()
  hourDistributionChart?.resize()
  dayOfWeekChart?.resize()
  errorTypeChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  userTrendChart?.dispose()
  submissionTrendChart?.dispose()
  hourDistributionChart?.dispose()
  dayOfWeekChart?.dispose()
  errorTypeChart?.dispose()
})
</script>

<style scoped>
.system-overview {
  padding: 10px;
}

.overview-cards {
  margin-bottom: 20px;
}

.stat-card {
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
