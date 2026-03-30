import request from '../utils/request'

export function listEmploymentAttachments() {
  return request.get('/employment-records/current/attachments')
}

export function createEmploymentAttachment(payload) {
  return request.post('/employment-records/current/attachments', payload)
}

export function deleteEmploymentAttachment(id) {
  return request.delete(`/employment-records/current/attachments/${id}`)
}
