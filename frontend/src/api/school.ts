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

export function getSchoolById(id: number) {
  return request({
    url: `/schools/${id}`,
    method: 'get'
  })
}

export function createSchool(data: any) {
  return request({
    url: '/schools',
    method: 'post',
    data
  })
}

export function updateSchool(id: number, data: any) {
  return request({
    url: `/schools/${id}`,
    method: 'put',
    data
  })
}

export function deleteSchool(id: number) {
  return request({
    url: `/schools/${id}`,
    method: 'delete'
  })
}

export function enableSchool(id: number, enabled: boolean) {
  return request({
    url: `/schools/${id}/enable`,
    method: 'put',
    data: { enabled }
  })
}
