import request from './request'

export function getClasses() {
  return request({
    url: '/classes',
    method: 'get'
  })
}

export function createClass(data: any) {
  return request({
    url: '/classes',
    method: 'post',
    data
  })
}

export function updateClass(id: number, data: any) {
  return request({
    url: `/classes/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id: number) {
  return request({
    url: `/classes/${id}`,
    method: 'delete'
  })
}

export function addStudentToClass(classId: number, studentId: number) {
  return request({
    url: `/classes/${classId}/students/${studentId}`,
    method: 'post'
  })
}

export function addStudentsToClass(classId: number, studentIds: number[]) {
  return request({
    url: `/classes/${classId}/students/batch`,
    method: 'post',
    data: studentIds
  })
}

export function removeStudentFromClass(classId: number, studentId: number) {
  return request({
    url: `/classes/${classId}/students/${studentId}`,
    method: 'delete'
  })
}

export function getClassStudents(classId: number) {
  return request({
    url: `/classes/${classId}/students`,
    method: 'get'
  })
}

export function getClassList(params?: any) {
  return request({
    url: '/classes',
    method: 'get',
    params
  })
}
