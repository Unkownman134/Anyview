import request from './request'

export function getSchools() {
  return request({
    url: '/schools',
    method: 'get'
  })
}

export function getEnabledSchools() {
  return request({
    url: '/schools/enabled',
    method: 'get'
  })
}

export function getSchoolById(id) {
  return request({
    url: `/schools/${id}`,
    method: 'get'
  })
}

export function createSchool(data) {
  return request({
    url: '/schools',
    method: 'post',
    data
  })
}

export function updateSchool(id, data) {
  return request({
    url: `/schools/${id}`,
    method: 'put',
    data
  })
}

export function deleteSchool(id) {
  return request({
    url: `/schools/${id}`,
    method: 'delete'
  })
}

export function enableSchool(id, enabled) {
  return request({
    url: `/schools/${id}/enable`,
    method: 'put',
    data: { enabled }
  })
}
