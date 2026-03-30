import request from '../utils/request'

export function getStatisticsOverview() {
  return request.get('/statistics/overview')
}

export function getEmploymentStatusStatistics() {
  return request.get('/statistics/employment-status')
}

export function getJobStatistics() {
  return request.get('/statistics/job')
}

export function getAdminDashboardOverview() {
  return request.get('/admin/dashboard/overview')
}
