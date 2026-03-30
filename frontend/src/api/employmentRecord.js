import request from '../utils/request'

export function getCurrentEmploymentRecord() {
  return request.get('/employment-records/current')
}

export function createCurrentEmploymentRecord(payload) {
  return request.post('/employment-records/current', payload)
}

export function updateCurrentEmploymentRecord(payload) {
  return request.put('/employment-records/current', payload)
}

export function listEmploymentReviewRecords() {
  return request.get('/employment-records/review')
}

export function getEmploymentRecordDetail(id) {
  return request.get(`/employment-records/${id}`)
}

export function reviewEmploymentRecord(id, payload) {
  return request.put(`/employment-records/${id}/review`, payload)
}
