<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索用户名、姓名或邮箱"
            prefix-icon="Search"
            style="width: 300px"
            @input="handleSearch"
          />
        </div>
      </template>
      <el-table 
        :data="filteredUsers" 
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="username" label="用户名" sortable />
        <el-table-column prop="realName" label="姓名" sortable />
        <el-table-column prop="email" label="邮箱" sortable />
        <el-table-column prop="role" label="角色" width="100" sortable>
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handleEdit(row)"
              :disabled="row.role === 'ADMIN'"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
              :disabled="row.role === 'ADMIN'"
            >
              删除
            </el-button>
            <el-button 
              size="small" 
              @click="handleToggleEnable(row)"
              :disabled="row.role === 'ADMIN'"
            >
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showEditDialog" title="编辑用户" width="500px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select 
            v-model="userForm.role" 
            style="width: 100%"
            :disabled="currentUser.role === 'ADMIN'"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch 
            v-model="userForm.enabled"
            :disabled="currentUser.role === 'ADMIN'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleEditSubmit"
          :disabled="currentUser.role === 'ADMIN'"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUser, deleteUser, enableUser } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const users = ref([])
const showEditDialog = ref(false)
const currentUser = ref(null)
const userForm = ref({
  username: '',
  realName: '',
  email: '',
  role: '',
  enabled: true
})
const searchQuery = ref('')
const sortField = ref('')
const sortOrder = ref('')

const loadUsers = async () => {
  try {
    const res = await getUsers()
    users.value = res.data || []
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

const filteredUsers = computed(() => {
  let result = [...users.value]
  
  // 搜索功能
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(user => 
      user.username.toLowerCase().includes(query) ||
      user.realName.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
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

const getRoleType = (role) => {
  const typeMap = {
    ADMIN: 'danger',
    TEACHER: 'warning',
    STUDENT: 'success'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生'
  }
  return textMap[role] || role
}

const handleEdit = (row) => {
  currentUser.value = row
  userForm.value = {
    username: row.username,
    realName: row.realName,
    email: row.email,
    role: row.role,
    enabled: row.enabled
  }
  showEditDialog.value = true
}

const handleEditSubmit = async () => {
  try {
    await updateUser(currentUser.value.id, userForm.value)
    ElMessage.success('编辑成功')
    showEditDialog.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error('编辑失败')
  }
}

const handleDelete = async (row) => {
  try {
    if (row.role === 'ADMIN') {
      ElMessage.warning('管理员账户不能删除')
      return
    }
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleToggleEnable = async (row) => {
  try {
    if (row.role === 'ADMIN') {
      ElMessage.warning('管理员账户不能禁用')
      return
    }
    await enableUser(row.id, !row.enabled)
    ElMessage.success(row.enabled ? '禁用成功' : '启用成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
