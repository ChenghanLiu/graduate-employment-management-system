import request from '../utils/request'

export function getCurrentStudentProfile() {
  return request.get('/student-profiles/current')
}

export function createCurrentStudentProfile(payload) {
  return request.post('/student-profiles/current', payload)
}

export function updateCurrentStudentProfile(payload) {
  return request.put('/student-profiles/current', payload)
}

export function listAdminStudents() {
  return request.get('/admin/students')
}

export function getAdminStudentDetail(id) {
  return request.get(`/admin/students/${id}`)
}

export function updateAdminStudent(id, payload) {
  return request.put(`/admin/students/${id}`, payload)
}

export function deleteAdminStudent(id) {
  return request.delete(`/admin/students/${id}`)
}
