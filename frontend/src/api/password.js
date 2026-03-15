import request from './request'

export function forgotPassword(email) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data: { email }
  })
}

export function resetPassword(token, newPassword) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data: { token, newPassword }
  })
}
