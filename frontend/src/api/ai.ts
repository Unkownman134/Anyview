import request from './request'

export function analyzeCode(data: any) {
  return request({
    url: '/ai/analyze',
    method: 'post',
    data
  })
}

export function gradeCode(data: any) {
  return request({
    url: '/ai/grade',
    method: 'post',
    data
  })
}

export function generateFeedback(data: any) {
  return request({
    url: '/ai/feedback',
    method: 'post',
    data
  })
}
