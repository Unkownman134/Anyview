<template>
  <div class="api-config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>API配置管理</span>
          <el-button type="primary" @click="showCreateDialog">新增配置</el-button>
        </div>
      </template>

      <el-table :data="apiConfigs" stripe>
        <el-table-column prop="name" label="配置名称" width="180" />
        <el-table-column prop="provider" label="提供商" width="150" />
        <el-table-column prop="apiUrl" label="API地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="model" label="模型" width="150" />
        <el-table-column prop="maxTokens" label="最大Token" width="120" />
        <el-table-column prop="temperature" label="温度" width="100" />
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button link type="info" @click="openChatTest(row)">聊天测试</el-button>
            <el-button link type="warning" @click="handleToggle(row)">
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑API配置' : '新增API配置'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="提供商" prop="provider">
          <el-input v-model="form.provider" placeholder="如：OpenAI、Anthropic" />
        </el-form-item>
        <el-form-item label="API地址" prop="apiUrl">
          <el-input v-model="form.apiUrl" placeholder="请输入API地址" />
        </el-form-item>
        <el-form-item label="API密钥" prop="apiKey">
          <el-input v-model="form.apiKey" type="password" placeholder="请输入API密钥" show-password />
        </el-form-item>
        <el-form-item label="模型" prop="model">
          <el-input v-model="form.model" placeholder="如：gpt-4、claude-3" />
        </el-form-item>

        <el-form-item label="最大Token" prop="maxTokens">
          <el-input-number v-model="form.maxTokens" :min="100" :max="10000" />
        </el-form-item>
        <el-form-item label="温度" prop="temperature">
          <el-input-number v-model="form.temperature" :min="0" :max="2" :step="0.1" :precision="1" />
        </el-form-item>
        <el-form-item label="启用状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 聊天测试对话框 -->
    <ChatTestDialog v-model="chatDialogVisible" :api-config="selectedConfig" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getApiConfigs,
  createApiConfig,
  updateApiConfig,
  toggleApiConfig,
  deleteApiConfig
} from '@/api/api-config'
import ChatTestDialog from '@/components/ChatTestDialog.vue'

const apiConfigs = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const chatDialogVisible = ref(false)
const selectedConfig = ref(null)
const form = ref({
  name: '',
  provider: '',
  apiUrl: '',
  apiKey: '',
  model: '',
  maxTokens: 2000,
  temperature: 0.7,
  enabled: true
})

const rules = {
  name: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  provider: [{ required: true, message: '请输入提供商', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入API地址', trigger: 'blur' }],
  apiKey: [{ required: true, message: '请输入API密钥', trigger: 'blur' }],
  model: [{ required: true, message: '请输入模型', trigger: 'blur' }]
}

const loadApiConfigs = async () => {
  try {
    const res = await getApiConfigs()
    if (res.code === 200) {
      apiConfigs.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载API配置失败')
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  form.value = {
    name: '',
    provider: '',
    apiUrl: '',
    apiKey: '',
    model: '',
    systemPrompt: '',
    maxTokens: 2000,
    temperature: 0.7,
    enabled: true
  }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateApiConfig(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createApiConfig(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadApiConfigs()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  }
}

const handleToggle = async (row) => {
  try {
    await toggleApiConfig(row.id)
    ElMessage.success(row.enabled ? '已禁用' : '已启用')
    loadApiConfigs()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteApiConfig(row.id)
    ElMessage.success('删除成功')
    loadApiConfigs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const openChatTest = (row) => {
  selectedConfig.value = row
  chatDialogVisible.value = true
}

onMounted(() => {
  loadApiConfigs()
})
</script>

<style scoped>
.api-config-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
