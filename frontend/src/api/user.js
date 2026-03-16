import request from './request'

export function getUsers() {
  return request({
    url: '/users',
    method: 'get'
  })
}

export function getUsersByRole(role) {
  return request({
    url: `/users/role/${role}`,
    method: 'get'
  })
}

export function getUsersByRoleAndSchool(role, schoolId) {
  return request({
    url: `/users/role/${role}/school/${schoolId}`,
    method: 'get'
  })
}

export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

export function enableUser(id, enabled) {
  return request({
    url: `/users/${id}/enable`,
    method: 'put',
    data: { enabled }
  })
}
