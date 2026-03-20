import request from './request'

export const getSystemAnalysis = (days: number = 30) => {
  return request({
    url: '/analysis/system',
    method: 'get',
    params: { days }
  })
}

export const getUserAnalysis = (userId: number) => {
  return request({
    url: `/analysis/user/${userId}`,
    method: 'get'
  })
}

export const getQuestionAnalysis = (questionId: number) => {
  return request({
    url: `/analysis/question/${questionId}`,
    method: 'get'
  })
}

export const getClassAnalysis = (classId: number) => {
  return request({
    url: `/analysis/class/${classId}`,
    method: 'get'
  })
}

export const recordLogin = () => {
  return request({
    url: '/analysis/activity/login',
    method: 'post'
  })
}
