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
        <el-table-column prop="gradeStatus" label="批改状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.gradeStatus === 'GRADED' ? 'success' : 'warning'">
              {{ row.gradeStatus === 'GRADED' ? '已批改' : '未批改' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="(isTeacher || isAdmin) && row.question?.type === 'fill'" size="small" type="primary" @click="handleGrade(row)">批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showGradeDialog" title="批改作业" width="700px">
      <div v-if="currentSubmission">
        <el-form :model="gradeForm" label-width="100px">
          <el-form-item label="学生">
            <span>{{ currentSubmission.student?.realName || '未知' }}</span>
          </el-form-item>
          <el-form-item label="题目">
            <span>{{ currentSubmission.question?.title || '未知' }}</span>
          </el-form-item>
          <el-form-item label="学生答案">
            <pre class="code-block">{{ currentSubmission.code }}</pre>
          </el-form-item>
          <el-form-item label="参考答案" v-if="currentSubmission.question?.referenceSolution">
            <pre class="code-block">{{ currentSubmission.question.referenceSolution }}</pre>
          </el-form-item>
          <el-form-item label="标准答案" v-if="currentSubmission.question?.type === 'fill' && currentSubmission.question?.answer">
            <pre class="code-block">{{ currentSubmission.question.answer }}</pre>
          </el-form-item>
          <el-form-item label="分数">
            <el-input-number v-model="gradeForm.score" :min="0" :max="maxScore" />
          </el-form-item>
          <el-form-item label="评语" required>
            <el-input v-model="gradeForm.teacherComment" type="textarea" :rows="4" placeholder="请输入评语" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showGradeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleGradeSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showViewDialog" title="查看提交" width="800px">
      <div v-if="viewSubmission">
        <el-form :model="viewSubmission" label-width="100px">
          <el-form-item label="学生">
            <span>{{ viewSubmission.student?.realName || '未知' }}</span>
          </el-form-item>
          <el-form-item label="作业">
            <span>{{ viewSubmission.assignment?.title || '未知' }}</span>
          </el-form-item>
          <el-form-item label="题目">
            <span>{{ viewSubmission.question?.title || '未知' }}</span>
          </el-form-item>
          <el-form-item label="答案">
            <pre class="code-block">{{ viewSubmission.code }}</pre>
          </el-form-item>
          <el-form-item label="参考答案" v-if="viewSubmission.question?.referenceSolution">
            <pre class="code-block">{{ viewSubmission.question.referenceSolution }}</pre>
          </el-form-item>
          <el-form-item label="标准答案" v-if="viewSubmission.question?.answer">
            <pre class="code-block">{{ viewSubmission.question.answer }}</pre>
          </el-form-item>
          <el-form-item label="分数">
            <span>{{ viewSubmission.score || 0 }}</span>
          </el-form-item>
          <el-form-item label="状态">
            <el-tag :type="viewSubmission.gradeStatus === 'GRADED' ? 'success' : 'warning'">
              {{ viewSubmission.gradeStatus === 'GRADED' ? '已批改' : '未批改' }}
            </el-tag>
          </el-form-item>
          <el-form-item label="教师评语" v-if="viewSubmission.teacherComment">
            <pre class="code-block">{{ viewSubmission.teacherComment }}</pre>
          </el-form-item>
          <el-form-item label="提交时间">
            <span>{{ viewSubmission.createdAt || '未知' }}</span>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showViewDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getSubmissions, gradeSubmission, getSubmissionById } from '@/api/submission'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const isTeacher = computed(() => userStore.user?.role === 'TEACHER')
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

const submissions = ref([])
const showGradeDialog = ref(false)
const currentSubmission = ref(null)
const maxScore = ref(100)
const gradeForm = reactive({
  score: 0,
  teacherComment: ''
})
const showAnswerDialog = ref(false)
const currentSubmissionForAnswer = ref(null)
const questionInfo = ref(null)

const loadSubmissions = async () => {
  try {
    const res = await getSubmissions()
    console.log('获取提交记录响应:', res)
    submissions.value = res.data || []
    console.log('提交记录列表:', submissions.value)
    submissions.value.forEach(sub => {
      console.log('提交记录详情:', {
        id: sub.id,
        student: sub.student,
        assignment: sub.assignment,
        question: sub.question,
        score: sub.score,
        gradeStatus: sub.gradeStatus
      })
    })
  } catch (error) {
    console.error('加载提交记录失败:', error)
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

const showViewDialog = ref(false)
const viewSubmission = ref(null)

const handleView = (row) => {
  console.log('查看提交记录:', row)
  console.log('学生信息:', row.student)
  console.log('作业信息:', row.assignment)
  console.log('题目信息:', row.question)
  viewSubmission.value = row
  showViewDialog.value = true
}

const handleGrade = async (row) => {
  currentSubmission.value = row
  gradeForm.score = row.score || 0
  gradeForm.teacherComment = row.teacherComment || ''
  
  // 获取题目信息，判断题目类型
  try {
    const res = await getSubmissionById(row.id)
    const question = res.data
    console.log('题目信息:', question)
    
    // 设置最大分数为题目分数
    maxScore.value = question?.score || 100
    
    // 只有填空题才需要教师批改，直接显示批改对话框
    showGradeDialog.value = true
  } catch (error) {
    console.error('获取题目信息失败:', error)
    ElMessage.error('获取题目信息失败')
  }
}

const handleGradeSubmit = async () => {
  if (!gradeForm.teacherComment || gradeForm.teacherComment.trim() === '') {
    ElMessage.error('请输入评语')
    return
  }
  
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
