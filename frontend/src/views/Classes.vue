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
        <el-table-column prop="school.name" label="学校" />
        <el-table-column prop="className" label="班级名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="teacher.realName" label="教师" />
        <el-table-column label="操作" width="320">
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
            <el-button 
              v-if="isAdmin"
              size="small" 
              type="primary"
              @click="handleAddStudents(row)"
            >
              添加学生
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="创建班级" width="500px">
      <el-form :model="classForm" label-width="80px">
        <el-form-item label="学校">
          <el-select v-model="classForm.schoolId" placeholder="请选择学校" style="width: 100%" @change="handleSchoolChange">
            <el-option
              v-for="school in schools"
              :key="school.id"
              :label="school.name"
              :value="school.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师">
          <el-select v-model="classForm.teacherId" placeholder="请先选择学校" style="width: 100%" :disabled="!classForm.schoolId">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.id"
              :label="teacher.realName"
              :value="teacher.id"
            />
          </el-select>
        </el-form-item>
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

    <el-dialog v-model="showStudentsDialog" title="班级学生信息" width="700px">
      <el-table :data="classStudents" style="width: 100%">
        <el-table-column prop="student.id" label="ID" width="80" />
        <el-table-column prop="student.username" label="学号" />
        <el-table-column prop="student.realName" label="姓名" />
        <el-table-column prop="student.email" label="邮箱" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button 
              v-if="isAdmin"
              size="small" 
              type="danger" 
              @click="handleRemoveStudent(row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showStudentsDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddStudentsDialog" title="添加学生到班级" width="700px">
      <div class="add-students-header">
        <span>班级：{{ currentClass?.className }} ({{ currentClass?.school?.name }})</span>
      </div>
      <el-table 
        ref="studentTableRef"
        :data="availableStudents" 
        style="width: 100%"
        @selection-change="handleStudentSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="学号" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
      </el-table>
      <template #footer>
        <el-button @click="showAddStudentsDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAddStudents" :disabled="selectedStudents.length === 0">
          确认添加 ({{ selectedStudents.length }}人)
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getClasses, createClass, updateClass, deleteClass, getClassStudents, addStudentsToClass, removeStudentFromClass } from '@/api/class'
import { getEnabledSchools } from '@/api/school'
import { getUsersByRoleAndSchool } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const classes = ref([])
const schools = ref([])
const teachers = ref([])
const showCreateDialog = ref(false)
const showStudentsDialog = ref(false)
const showAddStudentsDialog = ref(false)
const classStudents = ref([])
const availableStudents = ref([])
const selectedStudents = ref([])
const currentClass = ref(null)
const studentTableRef = ref(null)
const classForm = reactive({
  schoolId: null,
  teacherId: null,
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

const loadSchools = async () => {
  try {
    const res = await getEnabledSchools()
    schools.value = res.data || []
  } catch (error) {
    ElMessage.error('加载学校列表失败')
  }
}

const handleSchoolChange = async (schoolId) => {
  classForm.teacherId = null
  teachers.value = []
  if (schoolId) {
    await loadTeachersBySchool(schoolId)
  }
}

const loadTeachersBySchool = async (schoolId) => {
  try {
    const res = await getUsersByRoleAndSchool('TEACHER', schoolId)
    teachers.value = res.data || []
  } catch (error) {
    ElMessage.error('加载教师列表失败')
  }
}

const handleCreate = async () => {
  if (!classForm.schoolId) {
    ElMessage.error('请选择学校')
    return
  }
  if (!classForm.teacherId) {
    ElMessage.error('请选择教师')
    return
  }
  if (!classForm.className) {
    ElMessage.error('请输入班级名称')
    return
  }

  try {
    const classData = {
      schoolId: classForm.schoolId,
      teacherId: classForm.teacherId,
      className: classForm.className,
      description: classForm.description
    }
    await createClass(classData)
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
    await ElMessageBox.confirm('确定要删除该班级吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteClass(row.id)
    ElMessage.success('删除成功')
    loadClasses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleViewStudents = async (row) => {
  currentClass.value = row
  try {
    const res = await getClassStudents(row.id)
    classStudents.value = res.data || []
    showStudentsDialog.value = true
  } catch (error) {
    ElMessage.error('加载学生列表失败')
  }
}

const handleRemoveStudent = async (row) => {
  try {
    await ElMessageBox.confirm('确定要将该学生从班级中移除吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeStudentFromClass(currentClass.value.id, row.student.id)
    ElMessage.success('移除成功')
    const res = await getClassStudents(currentClass.value.id)
    classStudents.value = res.data || []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败')
    }
  }
}

const handleAddStudents = async (row) => {
  currentClass.value = row
  selectedStudents.value = []
  try {
    const res = await getUsersByRoleAndSchool('STUDENT', row.school.id)
    const existingRes = await getClassStudents(row.id)
    const existingStudentIds = (existingRes.data || []).map(cs => cs.student.id)
    availableStudents.value = (res.data || []).filter(s => !existingStudentIds.includes(s.id))
    showAddStudentsDialog.value = true
  } catch (error) {
    ElMessage.error('加载学生列表失败')
  }
}

const handleStudentSelectionChange = (selection) => {
  selectedStudents.value = selection
}

const handleConfirmAddStudents = async () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请选择要添加的学生')
    return
  }
  try {
    const studentIds = selectedStudents.value.map(s => s.id)
    await addStudentsToClass(currentClass.value.id, studentIds)
    ElMessage.success(`成功添加 ${selectedStudents.value.length} 名学生`)
    showAddStudentsDialog.value = false
    loadClasses()
  } catch (error) {
    ElMessage.error('添加学生失败')
  }
}

onMounted(() => {
  loadClasses()
  loadSchools()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.add-students-header {
  margin-bottom: 16px;
  font-weight: bold;
  color: #409eff;
}
</style>
