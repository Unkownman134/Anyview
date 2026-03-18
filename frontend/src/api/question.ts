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

export function createQuestion(data: any) {
  return request({
    url: '/questions',
    method: 'post',
    data
  })
}

export function updateQuestion(_id: number, data: any) {
  return request({
    url: '/questions/update',
    method: 'put',
    data
  })
}

export function deleteQuestion(id: number) {
  return request({
    url: `/questions/${id}`,
    method: 'delete'
  })
}
