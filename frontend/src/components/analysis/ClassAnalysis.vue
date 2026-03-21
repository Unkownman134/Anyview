<template>
  <div class="class-analysis">
    <el-row :gutter="20" class="search-row">
      <el-col :span="8">
        <el-select
          v-model="selectedClass"
          filterable
          placeholder="选择班级"
          @change="loadClassAnalysis"
          style="width: 100%"
        >
          <el-option
            v-for="cls in classOptions"
            :key="cls.id"
            :label="cls.name"
            :value="cls.id"
          />
        </el-select>
      </el-col>
    </el-row>

    <template v-if="analysisData">
      <el-row :gutter="20" class="info-row">
        <el-col :span="24">
          <el-card>
            <div class="class-info">
              <div>
                <h3>{{ analysisData.className }}</h3>
                <p class="teacher-name">教师: {{ analysisData.teacherName }}</p>
              </div>
              <div class="info-stats">
                <div class="info-stat">
                  <span class="label">学生数:</span>
                  <span class="value">{{ analysisData.totalStudents }}</span>
                </div>
                <div class="info-stat">
                  <span class="label">作业数:</span>
                  <span class="value">{{ analysisData.totalAssignments }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="stats-row">
        <el-col :span="6" v-for="stat in classStats" :key="stat.label">
          <el-card class="stat-item" :body-style="{ padding: '20px' }">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>学生排名</span>
            </template>
            <el-table :data="analysisData.studentPerformances" style="width: 100%" max-height="400">
              <el-table-column type="index" label="排名" width="70">
                <template #default="{ $index }">
                  <el-tag v-if="$index < 3" :type="['danger', 'warning', 'success'][$index]" effect="dark">
                    {{ $index + 1 }}
                  </el-tag>
                  <span v-else>{{ $index + 1 }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="realName" label="姓名" />
              <el-table-column prop="submissionCount" label="提交数" sortable />
              <el-table-column prop="solvedCount" label="解题数" sortable />
              <el-table-column prop="averageScore" label="平均分" sortable>
                <template #default="{ row }">
                  {{ row.averageScore?.toFixed(1) }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>成绩分布</span>
            </template>
            <div ref="scoreDistributionChartRef" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>作业完成情况</span>
            </template>
            <el-table :data="analysisData.assignmentPerformance" style="width: 100%">
              <el-table-column prop="assignmentName" label="作业名称" show-overflow-tooltip />
              <el-table-column prop="totalQuestions" label="题目数" width="100" />
              <el-table-column prop="submissionCount" label="提交数" width="100" sortable />
              <el-table-column prop="averageScore" label="平均分" width="100" sortable>
                <template #default="{ row }">
                  {{ row.averageScore?.toFixed(1) }}
                </template>
              </el-table-column>
              <el-table-column prop="completionRate" label="完成率" width="120" sortable>
                <template #default="{ row }">
                  <el-progress
                    :percentage="Math.round(row.completionRate || 0)"
                    :status="row.completionRate >= 80 ? 'success' : row.completionRate >= 60 ? '' : 'exception'"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="deadline" label="截止时间" width="180">
                <template #default="{ row }">
                  {{ row.deadline ? new Date(row.deadline).toLocaleString() : '-' }}
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else description="请选择班级查看分析数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed, markRaw } from 'vue'
import * as echarts from 'echarts'
import { User, Document, Check, Star } from '@element-plus/icons-vue'
import { getClassAnalysis } from '../../api/analysis'
import { getClasses } from '../../api/class'
import { ElMessage } from 'element-plus'

const selectedClass = ref<number | null>(null)
const classOptions = ref<any[]>([])
const analysisData = ref<any>(null)

const scoreDistributionChart = ref<echarts.ECharts | null>(null)
const scoreDistributionChartRef = ref<HTMLElement>()

const classStats = computed(() => {
  if (!analysisData.value) return []
  return [
    { label: '总学生', value: analysisData.value.totalStudents, icon: markRaw(User), color: '#409EFF' },
    { label: '总提交', value: analysisData.value.totalSubmissions, icon: markRaw(Document), color: '#67C23A' },
    { label: '平均分', value: analysisData.value.averageScore?.toFixed(1), icon: markRaw(Star), color: '#E6A23C' },
    { label: '通过率', value: analysisData.value.acceptanceRate?.toFixed(1) + '%', icon: markRaw(Check), color: '#F56C6C' }
  ]
})

const loadClassOptions = async () => {
  try {
    const res = await getClasses()
    if (res.code === 200) {
      classOptions.value = res.data || []
    }
  } catch (error) {
    console.error('加载班级列表失败', error)
  }
}

const loadClassAnalysis = async () => {
  if (!selectedClass.value) return
  try {
    const res = await getClassAnalysis(selectedClass.value)
    if (res.code === 200) {
      analysisData.value = res.data
      nextTick(() => {
        initScoreDistributionChart()
      })
    }
  } catch (error) {
    ElMessage.error('加载班级分析数据失败')
  }
}

const initScoreDistributionChart = () => {
  if (!scoreDistributionChartRef.value) return
  scoreDistributionChart.value = echarts.init(scoreDistributionChartRef.value)

  const data = analysisData.value?.scoreDistribution || []
  const option: any = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data.map((d: any) => ({ name: d.range, value: d.count })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        formatter: '{b}: {c}人 ({d}%)'
      }
    }]
  }
  scoreDistributionChart.value?.setOption(option)
}

const handleResize = () => {
  scoreDistributionChart.value?.resize()
}

onMounted(() => {
  loadClassOptions()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  scoreDistributionChart.value?.dispose()
})
</script>

<style scoped>
.class-analysis {
  padding: 10px;
}

.search-row {
  margin-bottom: 20px;
}

.info-row {
  margin-bottom: 20px;
}

.class-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.class-info h3 {
  margin: 0 0 10px 0;
}

.teacher-name {
  margin: 0;
  color: #909399;
}

.info-stats {
  display: flex;
  gap: 30px;
}

.info-stat {
  text-align: center;
}

.info-stat .label {
  color: #909399;
  margin-right: 5px;
}

.info-stat .value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  transition: transform 0.3s;
}

.stat-item:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 50px;
  height: 50px;
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
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.chart-row {
  margin-bottom: 20px;
}

.chart {
  height: 350px;
}
</style>
