<template>
  <div class="assignments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isStudent ? '作业列表' : '作业管理' }}</span>
          <el-button 
            v-if="!isStudent" 
            type="primary" 
            @click="showCreateDialog = true"
          >
            创建作业
          </el-button>
        </div>
      </template>
      <el-table :data="assignments" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column v-if="!isStudent" prop="classInfo.className" label="班级" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="published" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.published ? 'success' : 'info'">
              {{ row.published ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="!isStudent" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="!isStudent" 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button 
              v-if="isStudent" 
              size="small" 
              type="primary" 
              @click="handleSubmit(row)"
            >
              提交作业
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="创建作业" width="800px">
      <el-form :model="assignmentForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="assignmentForm.title" />
        </el-form-item>
        <el-form-item label="描述">
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
                <el-button size="small" type="danger" @click="removeQuestion(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showQuestionDialog" title="选择题目" width="800px">
      <el-input
        v-model="questionSearch"
        placeholder="搜索题目"
        prefix-icon="Search"
        style="margin-bottom: 10px"
      />
      <el-table :data="questions" style="width: 100%">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="difficulty" label="难度" width="100" />
        <el-table-column prop="category" label="分类" width="120" />
      </el-table>
      <template #footer>
        <el-button @click="showQuestionDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSelectQuestions">确认选择</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showSubmitDialog" title="提交作业" width="600px">
      <el-form :model="submitForm" label-width="100px">
        <el-form-item label="作业标题">
          <el-input v-model="currentAssignment.title" disabled />
        </el-form-item>
        <el-form-item label="作业描述">
          <el-input v-model="currentAssignment.description" type="textarea" disabled :rows="3" />
        </el-form-item>
        <el-form-item label="代码提交">
          <el-input v-model="submitForm.code" type="textarea" :rows="8" placeholder="请输入代码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSubmitDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitConfirm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getAssignments, createAssignment, updateAssignment, deleteAssignment } from '@/api/assignment'
import { getQuestions } from '@/api/question'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const assignments = ref([])
const questions = ref([])
const selectedQuestions = ref([])
const showCreateDialog = ref(false)
const showSubmitDialog = ref(false)
const showQuestionDialog = ref(false)
const currentAssignment = ref({})
const assignmentForm = reactive({
  title: '',
  description: '',
  startTime: '',
  endTime: ''
})
const submitForm = reactive({
  code: ''
})
const questionSearch = ref('')

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
    await createAssignment(assignmentForm)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    loadAssignments()
  } catch (error) {
    ElMessage.error('创建失败')
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

const loadQuestions = async () => {
  try {
    const res = await getQuestions()
    questions.value = res.data || []
  } catch (error) {
    ElMessage.error('加载题目列表失败')
  }
}

const confirmSelectQuestions = () => {
  // 模拟选择题目，实际应该获取选中的行
  const selected = [questions.value[0], questions.value[1]]
  selected.forEach(q => {
    selectedQuestions.value.push({ ...q, score: 10 })
  })
  showQuestionDialog.value = false
}

const removeQuestion = (row) => {
  const index = selectedQuestions.value.findIndex(q => q.id === row.id)
  if (index > -1) {
    selectedQuestions.value.splice(index, 1)
  }
}

const handleSubmitConfirm = async () => {
  try {
    // 模拟提交作业，实际应该调用API
    ElMessage.success('作业提交成功')
    showSubmitDialog.value = false
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  loadAssignments()
  if (!isStudent.value) {
    loadQuestions()
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
