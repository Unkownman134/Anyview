<template>
  <div class="schools-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>学校管理</h2>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索学校名称或代码"
              prefix-icon="Search"
              style="width: 250px; margin-right: 10px"
              @input="handleSearch"
            />
            <el-button type="primary" @click="handleAdd">添加学校</el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="filteredSchools" 
        v-loading="loading" 
        stripe
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="name" label="学校名称" sortable />
        <el-table-column prop="code" label="学校代码" sortable />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="enabled" label="状态" width="100" sortable>
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button size="small" @click="handleToggleEnable(row)">
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学校' : '添加学校'" width="500px">
      <el-form :model="schoolForm" :rules="rules" ref="schoolFormRef" label-width="100px">
        <el-form-item label="学校名称" prop="name">
          <el-input v-model="schoolForm.name" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="学校代码" prop="code">
          <el-input v-model="schoolForm.code" placeholder="请输入学校代码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="schoolForm.description" type="textarea" placeholder="请输入学校描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSchools, createSchool, updateSchool, deleteSchool, enableSchool } from '@/api/school'

const schools = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const schoolFormRef = ref()
const searchQuery = ref('')
const sortField = ref('')
const sortOrder = ref('')

const schoolForm = reactive({
  id: null,
  name: '',
  code: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入学校代码', trigger: 'blur' }]
}

const loadSchools = async () => {
  try {
    loading.value = true
    const res = await getSchools()
    schools.value = res.data || []
  } catch (error) {
    ElMessage.error('加载学校列表失败')
  } finally {
    loading.value = false
  }
}

const filteredSchools = computed(() => {
  let result = [...schools.value]
  
  // 搜索功能
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(school => 
      school.name.toLowerCase().includes(query) ||
      school.code.toLowerCase().includes(query)
    )
  }
  
  // 排序功能
  if (sortField.value) {
    result.sort((a, b) => {
      const aValue = a[sortField.value]
      const bValue = b[sortField.value]
      
      if (aValue < bValue) return sortOrder.value === 'ascending' ? -1 : 1
      if (aValue > bValue) return sortOrder.value === 'ascending' ? 1 : -1
      return 0
    })
  }
  
  return result
})

const handleSearch = () => {
  // 搜索逻辑已在computed中处理
}

const handleSortChange = (sort) => {
  sortField.value = sort.prop
  sortOrder.value = sort.order
}

const handleAdd = () => {
  isEdit.value = false
  schoolForm.id = null
  schoolForm.name = ''
  schoolForm.code = ''
  schoolForm.description = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  schoolForm.id = row.id
  schoolForm.name = row.name
  schoolForm.code = row.code
  schoolForm.description = row.description || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await schoolFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        if (isEdit.value) {
          await updateSchool(schoolForm.id, schoolForm)
          ElMessage.success('更新成功')
        } else {
          await createSchool(schoolForm)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadSchools()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学校吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSchool(row.id)
    ElMessage.success('删除成功')
    loadSchools()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleToggleEnable = async (row) => {
  try {
    await enableSchool(row.id, !row.enabled)
    ElMessage.success(row.enabled ? '禁用成功' : '启用成功')
    loadSchools()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadSchools()
})
</script>

<style scoped>
.schools-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
