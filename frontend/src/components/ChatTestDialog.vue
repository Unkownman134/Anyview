<template>
  <el-dialog
    v-model="visible"
    title="API连接测试"
    width="800px"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <div v-loading="loading" element-loading-text="测试中...">
      <div v-if="response">
        <div style="margin-bottom: 20px;">
          <strong>用户消息：</strong>
          <div style="margin-top: 10px; padding: 10px; background: #f5f5f5; border-radius: 4px;">
            你好，请介绍一下你自己
          </div>
        </div>
        <div>
          <strong>AI回复：</strong>
          <div style="margin-top: 10px; padding: 10px; background: #e6f7ff; border-radius: 4px; white-space: pre-wrap;">
            {{ response }}
          </div>
        </div>
      </div>
      <div v-else-if="error" style="color: red;">
        测试失败：{{ error }}
      </div>
    </div>
    
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="startTest" :loading="loading" :disabled="loading">
        测试
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: Boolean,
  apiConfig: Object
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const response = ref('')
const error = ref('')

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.apiConfig) {
    startTest()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const startTest = async () => {
  if (!props.apiConfig) {
    ElMessage.error('请先选择API配置')
    return
  }
  
  loading.value = true
  response.value = ''
  error.value = ''
  
  try {
    const { apiKey, apiUrl, model, maxTokens, temperature } = props.apiConfig
    
    const res = await fetch('/api/api-configs/chat-test', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
      },
      body: JSON.stringify({
        apiKey,
        apiUrl,
        model,
        maxTokens: maxTokens || 2000,
        temperature: temperature || 0.7
      })
    })
    
    const data = await res.json()
    
    if (data.code === 200) {
      response.value = data.data
    } else {
      error.value = data.message || '测试失败'
    }
  } catch (err) {
    error.value = err.message || '连接失败'
    ElMessage.error('测试连接失败：' + error.value)
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  visible.value = false
  response.value = ''
  error.value = ''
}
</script>
