import request from '../utils/request'

export function getCurrentEnterpriseProfile() {
  return request.get('/enterprise-profiles/current')
}

export function createCurrentEnterpriseProfile(payload) {
  return request.post('/enterprise-profiles/current', payload)
}

export function updateCurrentEnterpriseProfile(payload) {
  return request.put('/enterprise-profiles/current', payload)
}

export function getCurrentEnterpriseReviewStatus() {
  return request.get('/enterprise-profiles/current/review-status')
}

export function listAdminEnterprises() {
  return request.get('/admin/enterprises')
}

export function getAdminEnterpriseDetail(id) {
  return request.get(`/admin/enterprises/${id}`)
}

export function updateAdminEnterprise(id, payload) {
  return request.put(`/admin/enterprises/${id}`, payload)
}

export function deleteAdminEnterprise(id) {
  return request.delete(`/admin/enterprises/${id}`)
}

export function reviewEnterprise(id, payload) {
  return request.put(`/admin/enterprises/${id}/review`, payload)
}
