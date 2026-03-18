import request from './request'

export const getOverallStatistics = () => {
  return request({
    url: '/statistics/overall',
    method: 'get'
  })
}
