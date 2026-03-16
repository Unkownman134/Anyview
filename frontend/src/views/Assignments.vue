<template>
  <div class="assignments-container">
    <div class="assignments-header">
      <h2>作业管理</h2>
      <el-button v-if="!isStudent" type="primary" @click="showCreateDialog = true">创建作业</el-button>
    </div>
    
    <el-table :data="assignments" style="width: 100%">
      <el-table-column prop="id" label="作业ID" width="80" />
      <el-table-column prop="title" label="作业标题" />
      <el-table-column prop="description" label="作业描述" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column prop="published" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.published ? 'success' : 'info'">
            {{ row.published ? '已发布' : '未发布' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="!isStudent" type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="!isStudent" type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          <el-button v-else type="success" size="small" @click="handleSubmit(row)">提交作业</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 创建作业对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建作业"
      width="600px"
    >
      <el-form :model="assignmentForm" label-width="100px">
        <el-form-item label="作业标题">
          <el-input v-model="assignmentForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业描述">
          <el-input v-model="assignmentForm.description" type="textarea" :rows="4" />
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
        <el-form-item label="选择题目">
          <el-button type="primary" @click="showQuestionDialog = true">从题库选择题目</el-button>
          <el-table :data="selectedQuestions" style="width: 100%; margin-top: 10px">
            <el-table-column prop="id" label="题目ID" width="80" />
            <el-table-column prop="title" label="题目标题" />
            <el-table-column prop="score" label="分数" width="80">
              <template #default="{ row }">
                <el-input-number v-model="row.score" :min="1" :max="100" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="removeQuestion(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" @click="handleCreate">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 题目选择对话框 -->
    <el-dialog
      v-model="showQuestionDialog"
      title="选择题目"
      width="800px"
    >
      <el-input v-model="questionSearch" placeholder="搜索题目" style="margin-bottom: 10px" />
      <el-table
        ref="questionTable"
        :data="filteredQuestions"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="题目ID" width="80" />
        <el-table-column prop="title" label="题目标题" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            <el-tag :type="row.difficulty === 'EASY' ? 'success' : row.difficulty === 'MEDIUM' ? 'warning' : 'danger'">
              {{ row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showQuestionDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmSelectQuestions">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="showSubmitDialog"
      :title="`提交作业: ${currentAssignment.title}`"
      width="600px"
    >
      <el-form :model="submitForm" label-width="100px">
        <el-form-item label="代码">
          <el-input v-model="submitForm.code" type="textarea" :rows="10" placeholder="请输入代码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSubmitDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitCode">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElTag, ElButton, ElInput, ElTable, ElTableColumn, ElDialog, ElForm, ElFormItem, ElDatePicker, ElInputNumber } from 'element-plus'
import { getAssignments, deleteAssignment, createAssignmentWithQuestions } from '@/api/assignment'
import { getQuestions } from '@/api/question'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const assignments = ref([])
const showCreateDialog = ref(false)
const showQuestionDialog = ref(false)
const showSubmitDialog = ref(false)
const questions = ref([])
const selectedQuestions = ref([])
const currentAssignment = ref({})
const assignmentForm = reactive({
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  questionIds: []
})
const submitForm = reactive({
  code: ''
})
const questionSearch = ref('')
const questionTable = ref(null)
const currentSelection = ref([])

// 过滤后的题目列表
const filteredQuestions = computed(() => {
  if (!questionSearch.value) return questions.value
  return questions.value.filter(q => 
    q.title.toLowerCase().includes(questionSearch.value.toLowerCase())
  )
})

const isStudent = computed(() => userStore.user?.role === 'STUDENT')

const loadAssignments = async () => {
  try {
    const res = await getAssignments()
    assignments.value = res.data || []
  } catch (error) {
    ElMessage.error('加载作业列表失败')
  }
}

const handleCreate = async () => {
  try {
    // 构建作业数据
    const totalScore = selectedQuestions.value.reduce((sum, q) => sum + (q.score || 10), 0)
    
    // 处理日期格式
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
      published: false
    }
    
    // 构建题目关联数据
    const questionsData = selectedQuestions.value.map((q, index) => ({
      question: { id: q.id },
      score: q.score || 10,
      orderIndex: index + 1
    }))
    
    // 使用with-questions端点创建作业
    await createAssignmentWithQuestions({
      assignment: assignmentData,
      questions: questionsData
    })
    
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    // 重置表单
    assignmentForm.title = ''
    assignmentForm.description = ''
    assignmentForm.startTime = ''
    assignmentForm.endTime = ''
    assignmentForm.questionIds = []
    selectedQuestions.value = []
    loadAssignments()
  } catch (error) {
    ElMessage.error('创建失败：' + (error.response?.data?.message || error.message))
  }
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能待实现')
}

const handleDelete = async (row) => {
  try {
    await deleteAssignment(row.id)
    ElMessage.success('删除成功')
    loadAssignments()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSubmit = (row) => {
  currentAssignment.value = row
  submitForm.code = ''
  showSubmitDialog.value = true
}

const handleSubmitCode = async () => {
  ElMessage.info('提交功能待实现')
  showSubmitDialog.value = false
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
  
  ElMessage.success(`成功添加 ${newSelections.length} 道题目`)
  showQuestionDialog.value = false
  
  // 清空当前选择
  currentSelection.value = []
  if (questionTable.value) {
    questionTable.value.clearSelection()
  }
}

const removeQuestion = (row) => {
  const index = selectedQuestions.value.findIndex(q => q.id === row.id)
  if (index > -1) {
    selectedQuestions.value.splice(index, 1)
    // 更新assignmentForm中的questionIds
    assignmentForm.questionIds = selectedQuestions.value.map(q => q.id)
  }
}

onMounted(() => {
  loadAssignments()
  loadQuestions()
})
</script>

<style scoped>
.assignments-container {
  padding: 20px;
}

.assignments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
