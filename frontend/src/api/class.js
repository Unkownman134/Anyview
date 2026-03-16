import request from './request'

export function getClasses() {
  return request({
    url: '/classes',
    method: 'get'
  })
}

export function createClass(data) {
  return request({
    url: '/classes',
    method: 'post',
    data
  })
}

export function updateClass(id, data) {
  return request({
    url: `/classes/${id}`,
    method: 'put',
    data
  })
}

export function deleteClass(id) {
  return request({
    url: `/classes/${id}`,
    method: 'delete'
  })
}

export function addStudentToClass(classId, studentId) {
  return request({
    url: `/classes/${classId}/students/${studentId}`,
    method: 'post'
  })
}

export function addStudentsToClass(classId, studentIds) {
  return request({
    url: `/classes/${classId}/students/batch`,
    method: 'post',
    data: studentIds
  })
}

export function removeStudentFromClass(classId, studentId) {
  return request({
    url: `/classes/${classId}/students/${studentId}`,
    method: 'delete'
  })
}

export function getClassStudents(classId) {
  return request({
    url: `/classes/${classId}/students`,
    method: 'get'
  })
}
