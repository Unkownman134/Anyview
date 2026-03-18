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

export function createAssignment(data: any) {
  return request({
    url: '/assignments',
    method: 'post',
    data
  })
}

export function updateAssignment(id: number, data: any) {
  return request({
    url: `/assignments/${id}`,
    method: 'put',
    data
  })
}

export function deleteAssignment(id: number) {
  return request({
    url: `/assignments/${id}`,
    method: 'delete'
  })
}

export function publishAssignment(id: number) {
  return request({
    url: `/assignments/${id}/publish`,
    method: 'post'
  })
}

export function createAssignmentWithQuestions(data: any) {
  return request({
    url: '/assignments/with-questions',
    method: 'post',
    data
  })
}

export function getAssignmentById(id: number) {
  return request({
    url: `/assignments/${id}`,
    method: 'get'
  })
}

export function updateAssignmentData(id: number, data: any) {
  return request({
    url: `/assignments/${id}`,
    method: 'put',
    data
  })
}

export function updateAssignmentWithQuestions(id: number, data: any) {
  return request({
    url: `/assignments/${id}/with-questions`,
    method: 'put',
    data
  })
}
