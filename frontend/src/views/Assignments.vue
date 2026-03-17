<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAssignments, getPublishedAssignments, getAssignmentById, updateAssignmentWithQuestions, deleteAssignment, createAssignmentWithQuestions, publishAssignment } from '@/api/assignment'
import { getAssignmentSubmissions, createSubmission, gradeSubmission } from '@/api/submission'
import { getQuestions } from '@/api/question'
import { getClasses } from '@/api/class'
import { getUsersByRole } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 作业列表
const assignments = ref([])
const loading = ref(false)

// 对话框显示状态
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const showQuestionDialog = ref(false)
const showSubmitDialog = ref(false)
const showMySubmissionsDialog = ref(false)
const showCodeDialog = ref(false)
const isViewMode = ref(false)

// 当前选中的作业
const currentAssignment = ref(null)
const viewingAssignment = ref(null)
const currentSubmissionCode = ref('')

// 作业详情
const showAssignmentDetailsDialog = ref(false)
const currentAssignmentDetails = ref(null)
const assignmentSubmissions = ref([])

// 表单数据
const assignmentForm = reactive({
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  classId: null,
  questionIds: []
})

const submitForm = reactive({
  submissions: {}
})

const selectOption = (questionId, option) => {
  // 提取选项标识（如"A"、"B"等）
  const optionId = option.split('.')[0].trim()
  submitForm.submissions[questionId] = optionId
}

const mySubmissions = ref([])

// 数据列表
const questions = ref([])
const classes = ref([])
const selectedQuestions = ref([])
const currentSelection = ref([])

// 编辑中的作业ID
const editingAssignmentId = ref(null)

// 计算属性
const isAdmin = computed(() => userStore.user?.role === 'ADMIN')
const isTeacher = computed(() => userStore.user?.role === 'TEACHER')
const isStudent = computed(() => userStore.user?.role === 'STUDENT')

const canSubmit = computed(() => {
  if (!currentAssignment.value) return false
  const questions = currentAssignment.value.assignmentQuestions || []
  return questions.every(q => submitForm.submissions[q.questionId])
})

// 加载作业列表
const loadAssignments = async () => {
  loading.value = true
  try {
    console.log('开始加载作业列表，用户角色:', userStore.user?.role)
    let res
    if (isStudent.value) {
      console.log('学生用户，加载已发布作业')
      res = await getPublishedAssignments()
    } else if (isTeacher.value) {
      console.log('教师用户，加载所有作业')
      res = await getAssignments()
    } else {
      console.log('管理员用户，加载所有作业')
      res = await getAssignments()
    }
    console.log('作业列表响应:', res)
    assignments.value = res.data || []
    console.log('作业列表数量:', assignments.value.length)
  } catch (error) {
    console.error('加载作业列表失败:', error)
    ElMessage.error('加载作业列表失败')
  } finally {
    loading.value = false
  }
}

// 加载班级列表
const loadClasses = async () => {
  try {
    const res = await getClasses()
    classes.value = res.data || []
  } catch (error) {
    ElMessage.error('加载班级列表失败')
  }
}

// 打开创建对话框
const openCreateDialog = () => {
  resetForm()
  showCreateDialog.value = true
}

// 打开编辑对话框
const openEditDialog = async (row) => {
  await handleEdit(row)
}

// 处理创建
const handleCreate = async () => {
  if (!assignmentForm.classId) {
    ElMessage.error('请选择班级')
    return
  }
  
  if (selectedQuestions.value.length === 0) {
    ElMessage.error('请至少选择一道题目')
    return
  }
  
  try {
    console.log('开始创建作业')
    const totalScore = selectedQuestions.value.reduce((sum, q) => sum + (q.score || 10), 0)
    
    const formatDate = (date) => {
      if (!date) return ''
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      return date
    }
    
    const assignmentData = {
      title: assignmentForm.title,
      description: assignmentForm.description,
      startTime: formatDate(assignmentForm.startTime),
      endTime: formatDate(assignmentForm.endTime),
      totalScore: totalScore,
      classId: assignmentForm.classId,
      teacherId: userStore.user?.id
    }
    
    const questionsData = selectedQuestions.value.map((q, index) => ({
      questionId: q.id,
      score: q.score || 10,
      orderIndex: index + 1
    }))
    
    console.log('创建作业数据:', assignmentData)
    console.log('创建题目数据:', questionsData)
    
    await createAssignmentWithQuestions({
      assignment: assignmentData,
      questions: questionsData
    })
    
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    resetForm()
    loadAssignments()
  } catch (error) {
    console.error('创建作业失败:', error)
    ElMessage.error('创建失败：' + (error.response?.data?.message || error.message))
  }
}

// 处理更新
const handleUpdate = async () => {
  if (!assignmentForm.classId) {
    ElMessage.error('请选择班级')
    return
  }
  
  try {
    console.log('开始更新作业，作业ID:', editingAssignmentId.value)
    const totalScore = selectedQuestions.value.reduce((sum, q) => sum + (q.score || 10), 0)
    
    const formatDate = (date) => {
      if (!date) return ''
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        const seconds = String(date.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      return date
    }
    
    const assignmentData = {
      title: assignmentForm.title,
      description: assignmentForm.description,
      startTime: formatDate(assignmentForm.startTime),
      endTime: formatDate(assignmentForm.endTime),
      totalScore: totalScore,
      classId: assignmentForm.classId,
      teacherId: userStore.user?.id
    }
    
    const questionsData = selectedQuestions.value.map((q, index) => ({
      questionId: q.id,
      score: q.score || 10,
      orderIndex: index + 1
    }))
    
    console.log('更新作业数据:', assignmentData)
    console.log('更新题目数据:', questionsData)
    
    await updateAssignmentWithQuestions(editingAssignmentId.value, {
      assignment: assignmentData,
      questions: questionsData
    })
    
    ElMessage.success('更新成功')
    showEditDialog.value = false
    resetForm()
    loadAssignments()
  } catch (error) {
    console.error('更新作业失败:', error)
    ElMessage.error('更新失败：' + (error.response?.data?.message || error.message))
  }
}

const resetForm = () => {
  assignmentForm.title = ''
  assignmentForm.description = ''
  assignmentForm.startTime = ''
  assignmentForm.endTime = ''
  assignmentForm.classId = null
  assignmentForm.questionIds = []
  selectedQuestions.value = []
  editingAssignmentId.value = null
}

const handleEdit = async (row) => {
  try {
    console.log('开始编辑作业，作业ID:', row.id)
    const res = await getAssignmentById(row.id)
    console.log('获取作业详情响应:', res)
    const assignment = res.data
    console.log('作业详情:', assignment)
    console.log('作业题目数量:', assignment.assignmentQuestions?.length || 0)
    editingAssignmentId.value = assignment.id
    assignmentForm.title = assignment.title
    assignmentForm.description = assignment.description
    assignmentForm.startTime = assignment.startTime
    assignmentForm.endTime = assignment.endTime
    assignmentForm.classId = assignment.classId
    
    selectedQuestions.value = (assignment.assignmentQuestions || []).map(aq => ({
      id: aq.questionId,
      title: aq.questionTitle,
      type: aq.questionType,
      difficulty: aq.questionDifficulty,
      score: aq.score
    }))
    
    console.log('选中的题目:', selectedQuestions.value)
    assignmentForm.questionIds = selectedQuestions.value.map(q => q.id)
    showEditDialog.value = true
  } catch (error) {
    console.error('加载作业详情失败:', error)
    ElMessage.error('加载作业详情失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该作业吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAssignment(row.id)
    ElMessage.success('删除成功')
    loadAssignments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('发布后作业将不能再编辑，只能删除。确定要发布吗？', '发布确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await publishAssignment(row.id)
    ElMessage.success('发布成功')
    loadAssignments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const openQuestionDialog = () => {
  showQuestionDialog.value = true
  loadQuestions()
}

const removeQuestion = (index) => {
  selectedQuestions.value.splice(index, 1)
  assignmentForm.questionIds = selectedQuestions.value.map(q => q.id)
}

const openSubmitDialog = async (row) => {
  try {
    console.log('打开提交对话框，作业ID:', row.id)
    
    // 检查学生是否已经提交过该作业
    const studentId = userStore.user?.id
    const submissionsRes = await getAssignmentSubmissions(row.id)
    const submissions = submissionsRes.data || []
    const hasSubmitted = submissions.some(sub => sub.studentId === studentId)
    
    if (hasSubmitted) {
      ElMessage.warning('该作业只能提交一次，您已经提交过了')
      return
    }
    
    const res = await getAssignmentById(row.id)
    console.log('获取作业详情:', res)
    currentAssignment.value = res.data
    submitForm.submissions = {}
    
    // 初始化提交表单，编程题自动填充模板代码
    currentAssignment.value.assignmentQuestions.forEach(question => {
      if (question.questionType === 'programming' && question.questionTemplateCode) {
        submitForm.submissions[question.questionId] = question.questionTemplateCode
      } else {
        submitForm.submissions[question.questionId] = ''
      }
    })
    
    isViewMode.value = false
    showSubmitDialog.value = true
  } catch (error) {
    console.error('加载作业详情失败:', error)
    ElMessage.error('加载作业详情失败')
  }
}

const closeSubmitDialog = () => {
  showSubmitDialog.value = false
  isViewMode.value = false
  submitForm.submissions = {}
}

const handleAssignmentDetails = async (row) => {
  try {
    // 加载作业详情
    const assignmentRes = await getAssignmentById(row.id)
    currentAssignmentDetails.value = assignmentRes.data
    
    // 加载所有学生列表
    const studentsRes = await getUsersByRole('STUDENT')
    const allStudents = studentsRes.data || []
    
    // 加载作业的提交情况
    const submissionsRes = await getAssignmentSubmissions(row.id)
    const submissions = submissionsRes.data || []
    
    // 处理提交情况数据
    const submittedStudentIds = new Set(submissions.map(sub => sub.studentId))
    
    // 按学生ID分组提交记录
    const submissionsByStudent = {}
    submissions.forEach(sub => {
      if (!submissionsByStudent[sub.studentId]) {
        submissionsByStudent[sub.studentId] = []
      }
      submissionsByStudent[sub.studentId].push(sub)
    })
    
    // 生成完整的提交情况列表，包括已提交和未提交的学生
    assignmentSubmissions.value = allStudents.map(student => {
      const studentSubmissions = submissionsByStudent[student.id] || []
      const submitted = submittedStudentIds.has(student.id)
      
      // 计算总分：所有已批改提交的分数之和
      const totalScore = studentSubmissions
        .filter(sub => sub.gradeStatus === 'GRADED' && sub.score)
        .reduce((sum, sub) => sum + sub.score, 0)
      
      // 判断批改状态：所有提交都已批改
      const allGraded = studentSubmissions.length > 0 && 
        studentSubmissions.every(sub => sub.gradeStatus === 'GRADED')
      
      return {
        id: student.id,
        studentName: student.realName || student.username,
        submitted,
        graded: allGraded,
        score: totalScore
      }
    })
    
    showAssignmentDetailsDialog.value = true
  } catch (error) {
    console.error('加载作业详情失败:', error)
    ElMessage.error('加载作业详情失败')
  }
}

const closeAssignmentDetailsDialog = () => {
  showAssignmentDetailsDialog.value = false
  currentAssignmentDetails.value = null
  assignmentSubmissions.value = []
}

// 根据题目类型获取占位符
const getQuestionPlaceholder = (type) => {
  switch (type) {
    case 'single':
      return ''
    case 'multiple':
      return ''
    case 'fill':
      return '请输入答案'
    case 'programming':
      return '请输入代码'
    default:
      return '请输入答案或代码'
  }
}

const handleSubmitCode = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请完成所有题目后再提交')
    return
  }

  try {
    const submissions = []
    for (const questionId in submitForm.submissions) {
      submissions.push({
        assignmentId: currentAssignment.value.id,
        questionId: parseInt(questionId),
        studentId: userStore.user?.id,
        code: submitForm.submissions[questionId],
        status: 'PENDING',
        score: 0
      })
    }

    // 区分编程题和其他题型
    const programmingQuestions = currentAssignment.value.assignmentQuestions.filter(q => q.questionType === 'programming')
    const programmingQuestionIds = new Set(programmingQuestions.map(q => q.questionId))
    
    const programmingSubmissions = submissions.filter(s => programmingQuestionIds.has(s.questionId))
    const otherSubmissions = submissions.filter(s => !programmingQuestionIds.has(s.questionId))

    // 先异步提交编程题（不等待AI评分完成）
    if (programmingSubmissions.length > 0) {
      ElMessage.info('编程题正在后台处理，请稍后查看结果')
      Promise.all(programmingSubmissions.map(submission => createSubmission(submission))).catch(error => {
        console.error('编程题提交失败:', error)
        ElMessage.error('编程题提交失败：' + (error.response?.data?.message || error.message))
      })
    }

    // 再同步提交非编程题
    for (const submission of otherSubmissions) {
      await createSubmission(submission)
    }

    ElMessage.success('提交成功')
    showSubmitDialog.value = false
    submitForm.submissions = {}
  } catch (error) {
    ElMessage.error('提交失败：' + (error.response?.data?.message || error.message))
  }
}

const handleViewMySubmissions = async (row) => {
  try {
    console.log('查看我的提交记录，作业ID:', row.id)
    viewingAssignment.value = row
    const res = await getAssignmentSubmissions(row.id)
    console.log('提交记录响应:', res)
    
    const allSubmissions = res.data || []
    console.log('所有提交记录:', allSubmissions)
    
    const studentId = userStore.user?.id
    console.log('当前学生ID:', studentId)
    
    mySubmissions.value = allSubmissions.filter(s => s.student?.id === studentId)
    console.log('我的提交记录:', mySubmissions.value)
    
    showMySubmissionsDialog.value = true
  } catch (error) {
    console.error('加载提交记录失败:', error)
    ElMessage.error('加载提交记录失败')
  }
}

const viewMySubmissionCode = (row) => {
  currentSubmissionCode.value = row.code
  showCodeDialog.value = true
}

const loadQuestions = async () => {
  try {
    const res = await getQuestions()
    questions.value = res.data || []
  } catch (error) {
    ElMessage.error('加载题目列表失败')
  }
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  currentSelection.value = selection
}

const confirmSelectQuestions = () => {
  // 获取选中的题目，过滤掉已添加的
  const newSelections = currentSelection.value.filter(q => 
    !selectedQuestions.value.some(sq => sq.id === q.id)
  )
  
  if (newSelections.length === 0) {
    ElMessage.warning('所选题目已全部添加或没有选择新题目')
    showQuestionDialog.value = false
    return
  }
  
  newSelections.forEach(q => {
    selectedQuestions.value.push({ ...q, score: 10 })
  })
  
  // 更新assignmentForm中的questionIds
  assignmentForm.questionIds = selectedQuestions.value.map(q => q.id)
  
  showQuestionDialog.value = false
  ElMessage.success(`成功添加 ${newSelections.length} 道题目`)
}

const cancelSelectQuestions = () => {
  showQuestionDialog.value = false
}

// 获取题目类型标签
const getQuestionTypeLabel = (type) => {
  const typeMap = {
    'single': '单选题',
    'multiple': '多选题',
    'fill': '填空题',
    'programming': '编程题'
  }
  return typeMap[type] || type
}

// 获取难度标签
const getDifficultyLabel = (difficulty) => {
  const difficultyMap = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

// 获取难度标签类型
const getDifficultyType = (difficulty) => {
  const typeMap = {
    'easy': 'success',
    'medium': 'warning',
    'hard': 'danger'
  }
  return typeMap[difficulty] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    'PENDING': '待提交',
    'SUBMITTED': '已提交',
    'GRADED': '已批改'
  }
  return statusMap[status] || status
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'SUBMITTED': 'primary',
    'GRADED': 'success'
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  loadAssignments()
  loadClasses()
})
</script>

<template>
  <div class="assignments-container">
    <div class="header">
      <h2>{{ isStudent ? '作业列表' : '作业管理' }}</h2>
      <el-button v-if="isTeacher || isAdmin" type="primary" @click="openCreateDialog">
        创建作业
      </el-button>
    </div>

    <el-table :data="assignments" v-loading="loading" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column prop="endTime" label="结束时间" width="180" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.published ? 'success' : 'info'">
            {{ row.published ? '已发布' : '未发布' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button v-if="isStudent" type="primary" size="small" @click="openSubmitDialog(row)">
            提交作业
          </el-button>
          <template v-if="isTeacher || isAdmin">
            <el-button v-if="!row.published" type="primary" size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button type="success" size="small" @click="handlePublish(row)" v-if="!row.published">
              发布
            </el-button>
            <el-button v-if="row.published" type="info" size="small" @click="handleAssignmentDetails(row)">
              作业详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建作业对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建作业" width="800px">
      <el-form :model="assignmentForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="assignmentForm.description" type="textarea" :rows="3" placeholder="请输入作业描述" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="assignmentForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="cls in classes"
              :key="cls.id"
              :label="cls.name"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="assignmentForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="assignmentForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="题目">
          <div class="selected-questions">
            <el-table :data="selectedQuestions" style="width: 100%">
              <el-table-column prop="title" label="题目" />
              <el-table-column prop="type" label="类型" width="120">
                <template #default="{ row }">
                  {{ getQuestionTypeLabel(row.type) }}
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="{ row }">
                  <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                    {{ getDifficultyLabel(row.difficulty) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="分值" width="150">
                <template #default="{ row }">
                  <span>{{ row.score }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" @click="removeQuestion($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" @click="openQuestionDialog" style="margin-top: 10px">
              添加题目
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑作业对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑作业" width="800px">
      <el-form :model="assignmentForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="assignmentForm.description" type="textarea" :rows="3" placeholder="请输入作业描述" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="assignmentForm.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="cls in classes"
              :key="cls.id"
              :label="cls.name"
              :value="cls.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="assignmentForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="assignmentForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="题目">
          <div class="selected-questions">
            <el-table :data="selectedQuestions" style="width: 100%">
              <el-table-column prop="title" label="题目" />
              <el-table-column prop="type" label="类型" width="120">
                <template #default="{ row }">
                  {{ getQuestionTypeLabel(row.type) }}
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="100">
                <template #default="{ row }">
                  <el-tag :type="getDifficultyType(row.difficulty)" size="small">
                    {{ getDifficultyLabel(row.difficulty) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="分值" width="150">
                <template #default="{ row }">
                  <span>{{ row.score }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" @click="removeQuestion($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" @click="openQuestionDialog" style="margin-top: 10px">
              添加题目
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 选择题目对话框 -->
    <el-dialog v-model="showQuestionDialog" title="选择题目" width="900px">
      <el-table
        :data="questions"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="题目" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            {{ getQuestionTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)" size="small">
              {{ getDifficultyLabel(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="cancelSelectQuestions">取消</el-button>
        <el-button type="primary" @click="confirmSelectQuestions">确定</el-button>
      </template>
    </el-dialog>

    <!-- 提交作业对话框 -->
    <el-dialog v-model="showSubmitDialog" title="提交作业" width="800px">
      <div v-if="currentAssignment">
        <el-alert
          title="注意：该作业只能提交一次，请仔细检查后再提交！"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px"
        />
        <h3>{{ currentAssignment.title }}</h3>
        <p>{{ currentAssignment.description }}</p>
        <el-divider />
        <div v-for="question in currentAssignment.assignmentQuestions" :key="question.questionId" class="question-item">
          <h4>{{ question.questionTitle }}</h4>
          <p>{{ question.questionDescription }}</p>
          
          <!-- 显示选项（选择题） -->
          <div v-if="question.questionType === 'single' || question.questionType === 'multiple'" class="question-options">
            <p style="font-weight: bold; margin: 10px 0;">选项：</p>
            <div v-if="question.questionOptions" class="options-list">
              <div 
                v-for="(option, index) in JSON.parse(question.questionOptions)" 
                :key="index" 
                class="option-item"
                :class="{ selected: submitForm.submissions[question.questionId] === option.split('.')[0].trim() }"
                @click="selectOption(question.questionId, option)"
              >
                {{ option }}
              </div>
            </div>
            <div v-else class="no-options">
              <el-tag type="info">无选项信息</el-tag>
            </div>
          </div>
          
          <!-- 显示编程题信息 -->
          <div v-if="question.questionType === 'programming'" class="programming-info">
            <div v-if="question.questionSampleInput" class="sample-input">
              <p style="font-weight: bold; margin: 10px 0 5px 0;">样例输入：</p>
              <pre class="code-block">{{ question.questionSampleInput }}</pre>
            </div>
            <div v-if="question.questionSampleOutput" class="sample-output">
              <p style="font-weight: bold; margin: 10px 0 5px 0;">样例输出：</p>
              <pre class="code-block">{{ question.questionSampleOutput }}</pre>
            </div>
            <div v-if="question.questionTemplateCode" class="template-code">
              <p style="font-weight: bold; margin: 10px 0 5px 0;">模板代码：</p>
              <pre class="code-block">{{ question.questionTemplateCode }}</pre>
            </div>
          </div>
          
          <!-- 编程题和填空题显示输入框 -->
          <el-input
            v-if="question.questionType === 'programming' || question.questionType === 'fill'"
            v-model="submitForm.submissions[question.questionId]"
            type="textarea"
            :rows="5"
            :placeholder="getQuestionPlaceholder(question.questionType)"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="closeSubmitDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmitCode" :disabled="!canSubmit">
          提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 我的提交记录对话框 -->
    <el-dialog v-model="showMySubmissionsDialog" title="我的提交记录" width="900px">
      <el-table :data="mySubmissions" style="width: 100%">
        <el-table-column prop="question.title" label="题目" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100" />
        <el-table-column prop="createdAt" label="提交时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewMySubmissionCode(row)">
              查看代码
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 查看代码对话框 -->
    <el-dialog v-model="showCodeDialog" title="提交代码" width="800px">
      <pre class="code-block">{{ currentSubmissionCode }}</pre>
    </el-dialog>

    <!-- 作业详情对话框 -->
    <el-dialog v-model="showAssignmentDetailsDialog" title="作业详情" width="900px">
      <div v-if="currentAssignmentDetails">
        <h3>{{ currentAssignmentDetails.title }}</h3>
        <p>{{ currentAssignmentDetails.description }}</p>
        <el-divider />
        
        <h4>提交情况</h4>
        <el-table :data="assignmentSubmissions" style="width: 100%">
          <el-table-column prop="studentName" label="学生" width="150" />
          <el-table-column label="提交状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.submitted ? 'success' : 'warning'">
                {{ row.submitted ? '已提交' : '未提交' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="批改状态" width="120">
            <template #default="{ row }">
              <el-tag :type="row.graded ? 'success' : 'warning'">
                {{ row.graded ? '已批改' : '未批改' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100" />
        </el-table>
        
        <h4 style="margin-top: 20px">题目列表</h4>
        <div v-for="question in currentAssignmentDetails.assignmentQuestions" :key="question.questionId" class="question-item">
          <h5>{{ question.questionTitle }}</h5>
          <p>{{ question.questionDescription }}</p>
          <p>分值: {{ question.score }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="closeAssignmentDetailsDialog">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.assignments-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.selected-questions {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

.question-item {
  margin-bottom: 20px;
}

.question-item h4 {
  margin-bottom: 10px;
}

.code-block {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  font-family: 'Courier New', monospace;
}

/* 题目选项样式 */
.question-options {
  margin: 15px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.options-list {
  margin-top: 10px;
}

.option-item {
  margin: 5px 0;
  padding-left: 20px;
  position: relative;
  cursor: pointer;
  transition: background-color 0.3s;
}

.option-item:hover {
  background-color: #f0f9eb;
}

.option-item.selected {
  background-color: #e6f7f6;
  border-left: 3px solid #409eff;
}

.option-item.selected::before {
  content: '●';
  position: absolute;
  left: 0;
  top: 6px;
  color: #409eff;
  font-weight: bold;
  font-size: 12px;
}

/* 编程题信息样式 */
.programming-info {
  margin: 15px 0;
}

.sample-input,
.sample-output,
.template-code {
  margin-bottom: 15px;
}

.no-options {
  margin-top: 10px;
}
</style>
