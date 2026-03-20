import request from './request'

export function getUsers() {
  return request({
    url: '/users',
    method: 'get'
  })
}

export function getUsersByRole(role: string) {
  return request({
    url: `/users/role/${role}`,
    method: 'get'
  })
}

export function getUsersByRoleAndSchool(role: string, schoolId: number) {
  return request({
    url: `/users/role/${role}/school/${schoolId}`,
    method: 'get'
  })
}

export function getUserById(id: number) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function updateUser(id: number, data: any) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id: number) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

export function enableUser(id: number, enabled: boolean) {
  return request({
    url: `/users/${id}/enable`,
    method: 'put',
    data: { enabled }
  })
}

export function getUserList(params?: any) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}
