import request from '../utils/request'

export function listAdminUsers() {
  return request.get('/admin/users')
}

export function getAdminUserDetail(id) {
  return request.get(`/admin/users/${id}`)
}

export function createAdminUser(payload) {
  return request.post('/admin/users', payload)
}

export function updateAdminUser(id, payload) {
  return request.put(`/admin/users/${id}`, payload)
}

export function disableAdminUser(id) {
  return request.delete(`/admin/users/${id}`)
}

export function assignAdminUserRoles(id, payload) {
  return request.put(`/admin/users/${id}/roles`, payload)
}

export function listAdminRoles() {
  return request.get('/admin/roles')
}

export function listAdminAnnouncements() {
  return request.get('/admin/announcements')
}

export function getAdminAnnouncementDetail(id) {
  return request.get(`/admin/announcements/${id}`)
}

export function createAdminAnnouncement(payload) {
  return request.post('/admin/announcements', payload)
}

export function updateAdminAnnouncement(id, payload) {
  return request.put(`/admin/announcements/${id}`, payload)
}

export function deleteAdminAnnouncement(id) {
  return request.delete(`/admin/announcements/${id}`)
}

export function updateAdminAnnouncementStatus(id, payload) {
  return request.put(`/admin/announcements/${id}/status`, payload)
}
