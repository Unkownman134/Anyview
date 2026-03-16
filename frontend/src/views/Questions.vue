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
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="difficulty" label="难度" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="创建题目" width="600px">
      <el-form :model="questionForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="questionForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="questionForm.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="questionForm.type" style="width: 100%">
            <el-option label="编程题" value="PROGRAMMING" />
            <el-option label="选择题" value="CHOICE" />
            <el-option label="填空题" value="FILL_BLANK" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-input-number v-model="questionForm.difficulty" :min="1" :max="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
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
const questionForm = reactive({
  title: '',
  description: '',
  type: 'PROGRAMMING',
  difficulty: 1
})

const loadQuestions = async () => {
  try {
    const res = await getQuestions()
    questions.value = res.data || []
  } catch (error) {
    ElMessage.error('加载题目列表失败')
  }
}

const handleCreate = async () => {
  try {
    await createQuestion(questionForm)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    loadQuestions()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能待实现')
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
