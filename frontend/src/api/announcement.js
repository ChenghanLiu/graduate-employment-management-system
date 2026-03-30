import request from '../utils/request'

export function listPublishedAnnouncements() {
  return request.get('/announcements')
}

export function getPublishedAnnouncementDetail(id) {
  return request.get(`/announcements/${id}`)
}

export function listManageAnnouncements() {
  return request.get('/announcements/manage')
}

export function createManageAnnouncement(payload) {
  return request.post('/announcements', payload)
}

export function updateManageAnnouncement(id, payload) {
  return request.put(`/announcements/${id}`, payload)
}

export function updateManageAnnouncementStatus(id, payload) {
  return request.put(`/announcements/${id}/status`, payload)
}
