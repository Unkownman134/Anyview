import request from './request'

interface LoginRequest {
  username: string
  password: string
}

interface RegisterRequest {
  username: string
  password: string
  email: string
  realName: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
  schoolId?: number
  classId?: number
}

interface User {
  id: number
  username: string
  realName: string
  email: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT'
  schoolId?: number
  classId?: number
}

export function login(data: LoginRequest) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data: RegisterRequest) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getCurrentUser(): Promise<User> {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}
