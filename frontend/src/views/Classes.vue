<template>
  <div class="classes">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
          <el-button type="primary" @click="showCreateDialog = true">创建班级</el-button>
        </div>
      </template>
      <el-table :data="classes" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="teacher.realName" label="教师" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="创建班级" width="500px">
      <el-form :model="classForm" label-width="80px">
        <el-form-item label="班级名称">
          <el-input v-model="classForm.className" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="classForm.description" type="textarea" />
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
import { getClasses, createClass, updateClass, deleteClass } from '@/api/class'

const classes = ref([])
const showCreateDialog = ref(false)
const classForm = reactive({
  className: '',
  description: ''
})

const loadClasses = async () => {
  try {
    const res = await getClasses()
    classes.value = res.data || []
  } catch (error) {
    ElMessage.error('加载班级列表失败')
  }
}

const handleCreate = async () => {
  try {
    await createClass(classForm)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    loadClasses()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const handleEdit = (row) => {
  ElMessage.info('编辑功能待实现')
}

const handleDelete = async (row) => {
  try {
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    loadClasses()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadClasses()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
