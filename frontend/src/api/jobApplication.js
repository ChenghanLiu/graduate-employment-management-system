import request from '../utils/request'

export function createJobApplication(payload) {
  return request.post('/job-applications', payload)
}

export function listMyJobApplications() {
  return request.get('/job-applications/mine')
}

export function listReceivedJobApplications() {
  return request.get('/job-applications/received')
}

export function updateJobApplicationStatus(id, payload) {
  return request.put(`/job-applications/${id}/status`, payload)
}
