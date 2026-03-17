import request from './request'

export function getQuestions() {
  return request({
    url: '/questions',
    method: 'get'
  })
}

export function getPublicQuestions() {
  return request({
    url: '/questions/public',
    method: 'get'
  })
}

export function createQuestion(data) {
  return request({
    url: '/questions',
    method: 'post',
    data
  })
}

export function updateQuestion(id, data) {
  return request({
    url: '/questions/update',
    method: 'put',
    data
  })
}

export function deleteQuestion(id) {
  return request({
    url: `/questions/${id}`,
    method: 'delete'
  })
}
