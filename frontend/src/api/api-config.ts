import request from './request'

export function getApiConfigs() {
  return request({
    url: '/api-configs',
    method: 'get'
  })
}

export function getEnabledApiConfigs() {
  return request({
    url: '/api-configs/enabled',
    method: 'get'
  })
}

export function getActiveApiConfig() {
  return request({
    url: '/api-configs/active',
    method: 'get'
  })
}

export function getApiConfigById(id: number) {
  return request({
    url: `/api-configs/${id}`,
    method: 'get'
  })
}

export function createApiConfig(data: any) {
  return request({
    url: '/api-configs',
    method: 'post',
    data
  })
}

export function updateApiConfig(id: number, data: any) {
  return request({
    url: `/api-configs/${id}`,
    method: 'put',
    data
  })
}

export function toggleApiConfig(id: number) {
  return request({
    url: `/api-configs/${id}/toggle`,
    method: 'put'
  })
}

export function deleteApiConfig(id: number) {
  return request({
    url: `/api-configs/${id}`,
    method: 'delete'
  })
}

export function testApiConnection(data: any) {
  return request({
    url: '/api-configs/test',
    method: 'post',
    data
  })
}
