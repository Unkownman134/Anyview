<template>
  <div class="question-analysis">
    <el-row :gutter="20" class="search-row">
      <el-col :span="8">
        <el-select
          v-model="selectedQuestion"
          filterable
          remote
          reserve-keyword
          placeholder="搜索题目"
          :remote-method="searchQuestions"
          :loading="searching"
          @change="loadQuestionAnalysis"
          style="width: 100%"
        >
          <el-option
            v-for="question in questionOptions"
            :key="question.id"
            :label="question.title"
            :value="question.id"
          />
        </el-select>
      </el-col>
    </el-row>

    <template v-if="analysisData">
      <el-row :gutter="20" class="info-row">
        <el-col :span="24">
          <el-card>
            <div class="question-info">
              <h3>{{ analysisData.title }}</h3>
              <div class="info-tags">
                <el-tag :type="getDifficultyType(analysisData.difficulty)">
                  {{ analysisData.difficulty }}
                </el-tag>
                <el-tag type="info">{{ analysisData.type }}</el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="4" v-for="stat in questionStats" :key="stat.label">
          <el-card class="stat-item" :body-style="{ padding: '15px' }">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>提交趋势（最近30天）</span>
            </template>
            <div ref="submissionTrendChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>错误类型分布</span>
            </template>
            <div ref="errorTypeChart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>分数分布</span>
            </template>
            <div ref="scoreDistributionChart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>关键指标</span>
            </template>
            <div class="key-metrics">
              <div class="metric-item">
                <div class="metric-label">唯一尝试用户</div>
                <div class="metric-value">{{ analysisData.uniqueAttemptUsers }}</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">唯一解决用户</div>
                <div class="metric-value">{{ analysisData.uniqueSolvedUsers }}</div>
              </div>
              <div class="metric-item">
                <div class="metric-label">解决率</div>
                <div class="metric-value">
                  {{ analysisData.uniqueAttemptUsers > 0
                    ? ((analysisData.uniqueSolvedUsers / analysisData.uniqueAttemptUsers) * 100).toFixed(1)
                    : 0 }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else description="请选择题目查看分析数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { getQuestionAnalysis } from '../../api/analysis'
import { getQuestions } from '../../api/question'
import { ElMessage } from 'element-plus'

const selectedQuestion = ref<number | null>(null)
const searching = ref(false)
const questionOptions = ref<any[]>([])
const analysisData = ref<any>(null)

const submissionTrendChart = ref<echarts.ECharts | null>(null)
const errorTypeChart = ref<echarts.ECharts | null>(null)
const scoreDistributionChart = ref<echarts.ECharts | null>(null)

const submissionTrendChartRef = ref<HTMLElement>()
const errorTypeChartRef = ref<HTMLElement>()
const scoreDistributionChartRef = ref<HTMLElement>()

const questionStats = computed(() => {
  if (!analysisData.value) return []
  return [
    { label: '总提交', value: analysisData.value.totalSubmissions, color: '#409EFF' },
    { label: '已通过', value: analysisData.value.acceptedSubmissions, color: '#67C23A' },
    { label: '失败', value: analysisData.value.failedSubmissions, color: '#F56C6C' },
    { label: '通过率', value: analysisData.value.acceptanceRate?.toFixed(1) + '%', color: '#E6A23C' },
    { label: '平均分', value: analysisData.value.averageScore?.toFixed(1), color: '#909399' }
  ]
})

const searchQuestions = async (query: string) => {
  if (query.length < 2) return
  searching.value = true
  try {
    const res = await getQuestions()
    if (res.code === 200) {
      questionOptions.value = res.data || []
    }
  } catch (error) {
    console.error('搜索题目失败', error)
  } finally {
    searching.value = false
  }
}

const loadQuestionAnalysis = async () => {
  if (!selectedQuestion.value) return
  try {
    const res = await getQuestionAnalysis(selectedQuestion.value)
    if (res.code === 200) {
      analysisData.value = res.data
      nextTick(() => {
        initCharts()
      })
    }
  } catch (error) {
    ElMessage.error('加载题目分析数据失败')
  }
}

const initCharts = () => {
  initSubmissionTrendChart()
  initErrorTypeChart()
  initScoreDistributionChart()
}

const initSubmissionTrendChart = () => {
  if (!submissionTrendChartRef.value) return
  submissionTrendChart.value = echarts.init(submissionTrendChartRef.value)

  const trend = analysisData.value?.submissionTrend || []
  const option: any = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['提交数', '通过数'] },
    xAxis: { type: 'category', data: trend.map((t: any) => t.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name: '提交数',
        type: 'line',
        data: trend.map((t: any) => t.submissionCount),
        smooth: true,
        areaStyle: { opacity: 0.3 }
      },
      {
        name: '通过数',
        type: 'line',
        data: trend.map((t: any) => t.acceptedCount),
        smooth: true
      }
    ]
  }
  submissionTrendChart.value?.setOption(option)
}

const initErrorTypeChart = () => {
  if (!errorTypeChartRef.value) return
  errorTypeChart.value = echarts.init(errorTypeChartRef.value)

  const data = analysisData.value?.errorTypeDistribution || {}
  const option: any = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: Object.entries(data).map(([name, value]) => ({ name, value })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  errorTypeChart.value?.setOption(option)
}

const initScoreDistributionChart = () => {
  if (!scoreDistributionChartRef.value) return
  scoreDistributionChart.value = echarts.init(scoreDistributionChartRef.value)

  const data = analysisData.value?.scoreDistribution || []
  const option: any = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c}人 ({d}%)'
    },
    xAxis: { type: 'category', data: data.map((d: any) => d.range) },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: data.map((d: any) => ({
        value: d.count,
        d: d.percentage?.toFixed(1)
      })),
      itemStyle: {
        color: new (echarts as any).graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  }
  scoreDistributionChart.value?.setOption(option)
}

const getDifficultyType = (difficulty: string) => {
  const map: Record<string, string> = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return map[difficulty] || 'info'
}

const handleResize = () => {
  submissionTrendChart.value?.resize()
  errorTypeChart.value?.resize()
  scoreDistributionChart.value?.resize()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  submissionTrendChart.value?.dispose()
  errorTypeChart.value?.dispose()
  scoreDistributionChart.value?.dispose()
})
</script>

<style scoped>
.question-analysis {
  padding: 10px;
}

.search-row {
  margin-bottom: 20px;
}

.info-row {
  margin-bottom: 20px;
}

.question-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-info h3 {
  margin: 0;
}

.info-tags {
  display: flex;
  gap: 10px;
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

.key-metrics {
  display: flex;
  justify-content: space-around;
  padding: 20px;
}

.metric-item {
  text-align: center;
}

.metric-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.metric-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
}
</style>
