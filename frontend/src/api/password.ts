import request from './request'

export function forgotPassword(email: string) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data: { email }
  })
}

export function resetPassword(token: string, newPassword: string) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data: { token, newPassword }
  })
}
