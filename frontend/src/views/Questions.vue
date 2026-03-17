<template>
  <div class="questions">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
          <el-button type="primary" @click="showCreateDialog = true">创建题目</el-button>
        </div>
      </template>
      <el-table :data="questions" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="{ row }">
            {{ getDifficultyLabel(row.difficulty) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="创建题目" width="800px">
      <el-form :model="questionForm" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="questionForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="questionForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="questionForm.type" style="width: 100%" @change="handleTypeChange">
            <el-option label="编程题" value="programming" />
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multiple" />
            <el-option label="填空题" value="fill" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="questionForm.difficulty" style="width: 100%">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'single' || questionForm.type === 'multiple'" label="选项">
          <el-input v-model="questionForm.options" type="textarea" :rows="3" placeholder='JSON格式，例如：["A. 选项1", "B. 选项2"]' />
        </el-form-item>
        <el-form-item label="答案">
          <el-input v-model="questionForm.answer" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="模板代码">
          <el-input v-model="questionForm.templateCode" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="样例输入">
          <el-input v-model="questionForm.sampleInput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="样例输出">
          <el-input v-model="questionForm.sampleOutput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="测试用例">
          <el-input v-model="questionForm.testCases" type="textarea" :rows="3" placeholder="JSON格式，例如：[[1,2,3],6]" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="时间限制(ms)">
          <el-input-number v-model="questionForm.timeLimit" :min="100" :max="10000" :step="100" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="内存限制(KB)">
          <el-input-number v-model="questionForm.memoryLimit" :min="64" :max="102400" :step="64" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditDialog" title="编辑题目" width="800px">
      <el-form :model="questionForm" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="questionForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="questionForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="questionForm.type" style="width: 100%" @change="handleTypeChange">
            <el-option label="编程题" value="programming" />
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multiple" />
            <el-option label="填空题" value="fill" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="questionForm.difficulty" style="width: 100%">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'single' || questionForm.type === 'multiple'" label="选项">
          <el-input v-model="questionForm.options" type="textarea" :rows="3" placeholder='JSON格式，例如：["A. 选项1", "B. 选项2"]' />
        </el-form-item>
        <el-form-item label="答案">
          <el-input v-model="questionForm.answer" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="模板代码">
          <el-input v-model="questionForm.templateCode" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="样例输入">
          <el-input v-model="questionForm.sampleInput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="样例输出">
          <el-input v-model="questionForm.sampleOutput" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="测试用例">
          <el-input v-model="questionForm.testCases" type="textarea" :rows="3" placeholder="JSON格式，例如：[[1,2,3],6]" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="时间限制(ms)">
          <el-input-number v-model="questionForm.timeLimit" :min="100" :max="10000" :step="100" />
        </el-form-item>
        <el-form-item v-if="questionForm.type === 'programming'" label="内存限制(KB)">
          <el-input-number v-model="questionForm.memoryLimit" :min="64" :max="102400" :step="64" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api/question'

const questions = ref([])
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const editingQuestionId = ref(null)
const questionForm = reactive({
  title: '',
  description: '',
  type: 'programming',
  difficulty: 'easy',
  score: 10,
  options: '',
  answer: '',
  analysis: '',
  templateCode: '',
  sampleInput: '',
  sampleOutput: '',
  testCases: '',
  timeLimit: 1000,
  memoryLimit: 1024
})

const handleTypeChange = () => {
  if (questionForm.type !== 'programming') {
    questionForm.templateCode = ''
    questionForm.sampleInput = ''
    questionForm.sampleOutput = ''
    questionForm.testCases = ''
    questionForm.timeLimit = null
    questionForm.memoryLimit = null
  }
}

const getTypeLabel = (type) => {
  const typeMap = {
    'programming': '编程题',
    'single': '单选题',
    'multiple': '多选题',
    'fill': '填空题'
  }
  return typeMap[type] || type
}

const getDifficultyLabel = (difficulty) => {
  const difficultyMap = {
    1: '简单',
    2: '中等',
    3: '困难',
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

const loadQuestions = async () => {
  try {
    const res = await getQuestions()
    console.log('API Response:', res)
    console.log('Questions data:', res.data)
    questions.value = res.data || []
    console.log('Questions array:', questions.value)
  } catch (error) {
    console.error('Error loading questions:', error)
    ElMessage.error('加载题目列表失败')
  }
}

const handleCreate = async () => {
  try {
    await createQuestion(questionForm)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    resetForm()
    loadQuestions()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleEdit = (row) => {
  editingQuestionId.value = row.id
  questionForm.title = row.title
  questionForm.description = row.description
  questionForm.type = row.type
  questionForm.difficulty = row.difficulty
  questionForm.score = row.score
  questionForm.options = row.options || ''
  questionForm.answer = row.answer || ''
  questionForm.analysis = row.analysis || ''
  questionForm.templateCode = row.templateCode || ''
  questionForm.sampleInput = row.sampleInput || ''
  questionForm.sampleOutput = row.sampleOutput || ''
  questionForm.testCases = row.testCases || ''
  questionForm.timeLimit = row.timeLimit || 1000
  questionForm.memoryLimit = row.memoryLimit || 1024
  showEditDialog.value = true
}

const handleUpdate = async () => {
  try {
    await updateQuestion(editingQuestionId.value, questionForm)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    resetForm()
    loadQuestions()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const resetForm = () => {
  questionForm.title = ''
  questionForm.description = ''
  questionForm.type = 'programming'
  questionForm.difficulty = 'easy'
  questionForm.score = 10
  questionForm.options = ''
  questionForm.answer = ''
  questionForm.analysis = ''
  questionForm.templateCode = ''
  questionForm.sampleInput = ''
  questionForm.sampleOutput = ''
  questionForm.testCases = ''
  questionForm.timeLimit = 1000
  questionForm.memoryLimit = 1024
  editingQuestionId.value = null
}

const handleDelete = async (row) => {
  try {
    await deleteQuestion(row.id)
    ElMessage.success('删除成功')
    loadQuestions()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
