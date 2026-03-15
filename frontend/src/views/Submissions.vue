<template>
  <div class="submissions">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>提交记录</span>
        </div>
      </template>
      <el-table :data="submissions" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="student.realName" label="学生" />
        <el-table-column prop="assignment.title" label="作业" />
        <el-table-column prop="question.title" label="题目" />
        <el-table-column prop="score" label="分数" width="80" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleGrade(row)">批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showGradeDialog" title="批改作业" width="600px">
      <el-form :model="gradeForm" label-width="100px">
        <el-form-item label="分数">
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.teacherComment" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGradeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleGradeSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSubmissions, gradeSubmission } from '@/api/submission'

const submissions = ref([])
const showGradeDialog = ref(false)
const currentSubmission = ref(null)
const gradeForm = reactive({
  score: 0,
  teacherComment: ''
})

const loadSubmissions = async () => {
  try {
    const res = await getSubmissions()
    submissions.value = res.data || []
  } catch (error) {
    ElMessage.error('加载提交记录失败')
  }
}

const getStatusType = (status) => {
  const typeMap = {
    ACCEPTED: 'success',
    WRONG_ANSWER: 'danger',
    TIME_LIMIT_EXCEEDED: 'warning',
    MEMORY_LIMIT_EXCEEDED: 'warning',
    COMPILATION_ERROR: 'danger',
    RUNTIME_ERROR: 'danger',
    PENDING: 'info',
    RUNNING: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    ACCEPTED: '通过',
    WRONG_ANSWER: '答案错误',
    TIME_LIMIT_EXCEEDED: '超时',
    MEMORY_LIMIT_EXCEEDED: '超内存',
    COMPILATION_ERROR: '编译错误',
    RUNTIME_ERROR: '运行错误',
    PENDING: '等待中',
    RUNNING: '运行中'
  }
  return textMap[status] || status
}

const handleView = (row) => {
  ElMessage.info('查看功能待实现')
}

const handleGrade = (row) => {
  currentSubmission.value = row
  gradeForm.score = row.score || 0
  gradeForm.teacherComment = row.teacherComment || ''
  showGradeDialog.value = true
}

const handleGradeSubmit = async () => {
  try {
    await gradeSubmission(currentSubmission.value.id, gradeForm)
    ElMessage.success('批改成功')
    showGradeDialog.value = false
    loadSubmissions()
  } catch (error) {
    ElMessage.error('批改失败')
  }
}

onMounted(() => {
  loadSubmissions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
