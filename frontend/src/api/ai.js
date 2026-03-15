import request from './request'

export function analyzeCode(data) {
  return request({
    url: '/ai/analyze',
    method: 'post',
    data
  })
}

export function gradeCode(data) {
  return request({
    url: '/ai/grade',
    method: 'post',
    data
  })
}

export function generateFeedback(data) {
  return request({
    url: '/ai/feedback',
    method: 'post',
    data
  })
}
