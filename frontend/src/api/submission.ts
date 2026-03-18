import request from './request'

export function getSubmissions() {
  return request({
    url: '/submissions',
    method: 'get'
  })
}

export function getStudentSubmissions(studentId: number) {
  return request({
    url: `/submissions/student/${studentId}`,
    method: 'get'
  })
}

export function getAssignmentSubmissions(assignmentId: number) {
  return request({
    url: `/submissions/assignment/${assignmentId}`,
    method: 'get'
  })
}

export function createSubmission(data: any) {
  return request({
    url: '/submissions',
    method: 'post',
    data
  })
}

export function gradeSubmission(id: number, data: any) {
  return request({
    url: `/submissions/${id}/grade`,
    method: 'post',
    data
  })
}

export function getSubmissionById(id: number) {
  return request({
    url: `/submissions/${id}`,
    method: 'get'
  })
}
