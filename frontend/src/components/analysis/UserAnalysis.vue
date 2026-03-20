<template>
  <div class="user-analysis">
    <el-row :gutter="20" class="search-row">
      <el-col :span="8">
        <el-select
          v-model="selectedUser"
          filterable
          remote
          reserve-keyword
          placeholder="搜索用户"
          :remote-method="searchUsers"
          :loading="searching"
          @change="loadUserAnalysis"
          style="width: 100%"
        >
          <el-option
            v-for="user in userOptions"
            :key="user.id"
            :label="`${user.realName} (${user.username})`"
            :value="user.id"
          />
        </el-select>
      </el-col>
    </el-row>

    <template v-if="analysisData">
      <el-row :gutter="20" class="stats-row">
        <el-col :span="4" v-for="stat in userStats" :key="stat.label">
          <el-card class="stat-item" :body-style="{ padding: '15px' }">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="16">
          <el-card>
            <template #header>
              <span>活动趋势（最近30天）</span>
            </template>
            <div ref="activityTrendChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>提交难度分布</span>
            </template>
            <div ref="difficultyChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>题目进度</span>
                <el-radio-group v-model="progressFilter" size="small">
                  <el-radio-button label="all">全部</el-radio-button>
                  <el-radio-button label="solved">已解决</el-radio-button>
                  <el-radio-button label="unsolved">未解决</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <el-table :data="filteredProgress" style="width: 100%" max-height="500">
              <el-table-column prop="questionTitle" label="题目" show-overflow-tooltip />
              <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="{ row }">
                  <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                    {{ row.difficulty }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="attempts" label="尝试次数" width="100" sortable />
              <el-table-column prop="bestScore" label="最高分" width="100" sortable />
              <el-table-column prop="solved" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.solved ? 'success' : 'info'" size="small">
                    {{ row.solved ? '已解决' : '未解决' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="lastSubmitTime" label="最后提交" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.lastSubmitTime) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else description="请选择用户查看分析数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { getUserAnalysis } from '../../api/analysis'
import { getUsers } from '../../api/user'
import { ElMessage } from 'element-plus'

const selectedUser = ref<number | null>(null)
const searching = ref(false)
const userOptions = ref<any[]>([])
const analysisData = ref<any>(null)
const progressFilter = ref('all')

let activityTrendChart: echarts.ECharts | null = null
let difficultyChart: echarts.ECharts | null = null

const activityTrendChartRef = ref<HTMLElement>()
const difficultyChartRef = ref<HTMLElement>()

const userStats = computed(() => {
  if (!analysisData.value) return []
  return [
    { label: '总提交', value: analysisData.value.totalSubmissions, color: '#409EFF' },
    { label: '已通过', value: analysisData.value.acceptedSubmissions, color: '#67C23A' },
    { label: '失败', value: analysisData.value.failedSubmissions, color: '#F56C6C' },
    { label: '通过率', value: analysisData.value.acceptanceRate?.toFixed(1) + '%', color: '#E6A23C' },
    { label: '平均分', value: analysisData.value.averageScore?.toFixed(1), color: '#909399' },
    { label: '解题数', value: analysisData.value.totalQuestionsSolved, color: '#409EFF' }
  ]
})

const filteredProgress = computed(() => {
  if (!analysisData.value?.questionProgress) return []
  const progress = analysisData.value.questionProgress
  if (progressFilter.value === 'solved') {
    return progress.filter((p: any) => p.solved)
  } else if (progressFilter.value === 'unsolved') {
    return progress.filter((p: any) => !p.solved)
  }
  return progress
})

const searchUsers = async (query: string) => {
  if (query.length < 2) return
  searching.value = true
  try {
    const res = await getUsers()
    if (res.code === 200) {
      userOptions.value = res.data || []
    }
  } catch (error) {
    console.error('搜索用户失败', error)
  } finally {
    searching.value = false
  }
}

const loadUserAnalysis = async () => {
  if (!selectedUser.value) return
  try {
    const res = await getUserAnalysis(selectedUser.value)
    if (res.code === 200) {
      analysisData.value = res.data
      nextTick(() => {
        initActivityTrendChart()
        initDifficultyChart()
      })
    }
  } catch (error) {
    ElMessage.error('加载用户分析数据失败')
  }
}

const initActivityTrendChart = () => {
  if (!activityTrendChartRef.value) return
  activityTrendChart = echarts.init(activityTrendChartRef.value)

  const trend = analysisData.value?.activityTrend || []
  const option: any = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['提交数', '登录次数', '学习时长(分钟)'] },
    xAxis: { type: 'category', data: trend.map((t: any) => t.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '提交数',
        type: 'line',
        data: trend.map((t: any) => t.submissionCount),
        smooth: true
      },
      {
        name: '登录次数',
        type: 'line',
        data: trend.map((t: any) => t.loginCount),
        smooth: true
      },
      {
        name: '学习时长(分钟)',
        type: 'bar',
        data: trend.map((t: any) => t.timeSpentMinutes)
      }
    ]
  }
  activityTrendChart.setOption(option)
}

const initDifficultyChart = () => {
  if (!difficultyChartRef.value) return
  difficultyChart = echarts.init(difficultyChartRef.value)

  const data = analysisData.value?.submissionsByDifficulty || {}
  const option: any = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { name: '简单', value: data.easy || 0, itemStyle: { color: '#67C23A' } },
        { name: '中等', value: data.medium || 0, itemStyle: { color: '#E6A23C' } },
        { name: '困难', value: data.hard || 0, itemStyle: { color: '#F56C6C' } }
      ]
    }]
  }
  difficultyChart.setOption(option)
}

const getDifficultyType = (difficulty: string) => {
  const map: Record<string, string> = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return map[difficulty] || 'info'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString()
}

const handleResize = () => {
  activityTrendChart?.resize()
  difficultyChart?.resize()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  activityTrendChart?.dispose()
  difficultyChart?.dispose()
})
</script>

<style scoped>
.user-analysis {
  padding: 10px;
}

.search-row {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
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
