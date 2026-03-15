import request from './request'

export function getSubmissions() {
  return request({
    url: '/submissions',
    method: 'get'
  })
}

export function getStudentSubmissions(studentId) {
  return request({
    url: `/submissions/student/${studentId}`,
    method: 'get'
  })
}

export function getAssignmentSubmissions(assignmentId) {
  return request({
    url: `/submissions/assignment/${assignmentId}`,
    method: 'get'
  })
}

export function createSubmission(data) {
  return request({
    url: '/submissions',
    method: 'post',
    data
  })
}

export function gradeSubmission(id, data) {
  return request({
    url: `/submissions/${id}/grade`,
    method: 'post',
    data
  })
}
