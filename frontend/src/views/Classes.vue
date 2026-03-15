<template>
  <div class="classes">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
          <el-button 
            v-if="isAdmin" 
            type="primary" 
            @click="showCreateDialog = true"
          >
            创建班级
          </el-button>
        </div>
      </template>
      <el-table :data="classes" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="teacher.realName" label="教师" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button 
              v-if="isAdmin" 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="isAdmin" 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button 
              size="small" 
              @click="handleViewStudents(row)"
            >
              查看学生
            </el-button>
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

    <el-dialog v-model="showStudentsDialog" title="班级学生信息" width="600px">
      <el-table :data="classStudents" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="学号" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
      </el-table>
      <template #footer>
        <el-button @click="showStudentsDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getClasses, createClass, updateClass, deleteClass } from '@/api/class'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const classes = ref([])
const showCreateDialog = ref(false)
const showStudentsDialog = ref(false)
const classStudents = ref([])
const currentClass = ref(null)
const classForm = reactive({
  className: '',
  description: ''
})

const isAdmin = computed(() => userStore.user?.role === 'ADMIN')

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

const handleViewStudents = (row) => {
  currentClass.value = row
  // 模拟班级学生数据，实际应该从API获取
  classStudents.value = [
    { id: 1, username: '2021001', realName: '张三', email: 'zhangsan@example.com' },
    { id: 2, username: '2021002', realName: '李四', email: 'lisi@example.com' },
    { id: 3, username: '2021003', realName: '王五', email: 'wangwu@example.com' }
  ]
  showStudentsDialog.value = true
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
