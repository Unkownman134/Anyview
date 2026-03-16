import request from './request'

export function getAssignments() {
  return request({
    url: '/assignments',
    method: 'get'
  })
}

export function getPublishedAssignments() {
  return request({
    url: '/assignments/published',
    method: 'get'
  })
}

export function createAssignment(data) {
  return request({
    url: '/assignments',
    method: 'post',
    data
  })
}

export function updateAssignment(id, data) {
  return request({
    url: `/assignments/${id}`,
    method: 'put',
    data
  })
}

export function deleteAssignment(id) {
  return request({
    url: `/assignments/${id}`,
    method: 'delete'
  })
}

export function publishAssignment(id) {
  return request({
    url: `/assignments/${id}/publish`,
    method: 'post'
  })
}

export function createAssignmentWithQuestions(data) {
  return request({
    url: '/assignments/with-questions',
    method: 'post',
    data
  })
}
