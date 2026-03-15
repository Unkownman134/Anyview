<template>
  <div class="assignments">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>作业管理</span>
          <el-button type="primary" @click="showCreateDialog = true">创建作业</el-button>
        </div>
      </template>
      <el-table :data="assignments" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="classInfo.className" label="班级" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="published" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.published ? 'success' : 'info'">
              {{ row.published ? '已发布' : '未发布' }}
            </el-tag>
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

    <el-dialog v-model="showCreateDialog" title="创建作业" width="600px">
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
import { getAssignments, createAssignment, updateAssignment, deleteAssignment } from '@/api/assignment'

const assignments = ref([])
const showCreateDialog = ref(false)
const assignmentForm = reactive({
  title: '',
  description: '',
  startTime: '',
  endTime: ''
})

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

onMounted(() => {
  loadAssignments()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
